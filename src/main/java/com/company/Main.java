package com.company;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.*;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.nio.file.*;

public class Main {

    private static final String EXCEL_FILE_NAME = "Clients.xls";
    private static final String RANDOMUSER_ME_API = "https://randomuser.me/api/";

    public static void main(String[] args)  {
        Random r = new Random();
        int n = 1 + r.nextInt(30);
        Client[] clients;

        try {
            clients = requestClientsFromAPI(n);
            saveClientsToDB(clients);
        } catch (UnknownHostException exception) {
            System.out.println("Connection to Internet is broken (can't load data from " + RANDOMUSER_ME_API
                    + "). Getting data from DB.");
            clients = requestClientsFromDB(n);
            if (clients == null) {
                System.out.println("Could not get data from DB. Generating data from local resources.");
                clients = generateClientsFromFiles(r, n);
            }
        } catch (IOException ioaEx) {
            System.out.println("Fatal error: "+ ioaEx.getMessage());
            clients = generateClientsFromFiles(r, n);
        }

        generateExcel(clients);
    }

    private static void saveClientsToDB(Client[] clients) {
        try(Connection conn = getConnection()) {
            for (Client client:clients) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet existingPersonRs = stmt.executeQuery(
                            "SELECT id, address_id FROM `person` WHERE " +
                            "surname = \"" + client.getSurname() + "\" AND " +
                            "name = \"" + client.getName()+ "\" AND " +
                            "middlename = \"" + client.getPatronomic() + "\"");
                    if (existingPersonRs.next()) {
                        // Person already exists in DB - do update
                        int personId = existingPersonRs.getInt(1);
                        int addressId = existingPersonRs.getInt(2);

                        stmt.executeUpdate("UPDATE `address` SET " +
                                "postcode = \"" + client.getIndex() + "\", " +
                                "country = \"" + client.getCountry() + "\", " +
                                "region = \"" + client.getRegion() + "\", " +
                                "city = \"" + client.getCity() + "\", " +
                                "street = \""+ client.getStreet() + "\", " +
                                "house = \"" + client.getHouse() + "\", " +
                                "flat = \"" + client.getFlat() + "\" "+
                                "WHERE id = " + addressId +";");

                        stmt.executeUpdate("UPDATE `person` SET " +
                                "birthday = \"" + client.getBDForDB() + "\", " +
                                "gender = \"" + client.getGender() + "\", " +
                                "inn = \"" + client.getInn() + "\" " +
                                "WHERE id = " + personId +";");

                    } else {
                        // new person - insert to DB
                        stmt.executeUpdate("INSERT INTO `address` (postcode, country, region, city, street, house, flat) VALUES (\"" +
                                client.getIndex() + "\", \"" +
                                client.getCountry() + "\", \"" +
                                client.getRegion() + "\", \"" +
                                client.getCity() + "\", \"" +
                                client.getStreet() + "\", \"" +
                                client.getHouse() + "\", \"" +
                                client.getFlat() + "\");", Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = stmt.getGeneratedKeys();
                        rs.next();
                        long addrKey = rs.getLong(1);
                        stmt.executeUpdate("INSERT INTO `person` (surname, name, middlename, birthday, gender, inn, address_id) VALUES (\"" +
                                client.getSurname() + "\", \"" +
                                client.getName() + "\", \"" +
                                client.getPatronomic() + "\", \"" +
                                client.getBDForDB() + "\", \"" +
                                client.getGender() + "\", \"" +
                                client.getInn() + "\", \"" +
                                addrKey + "\");");
                    }
                } catch (SQLException e) {
                    System.out.println("Can add client info to DB:");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Could not connect to DB to save clients:");
            e.printStackTrace();
        }
    }

    private static Client[] requestClientsFromDB(int n) {
        try(Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT name, surname, middlename, birthday, gender, inn, postcode, country, region, city, street, house, flat FROM `person` JOIN `address` ON address.id = person.address_id LIMIT " + n);
                if (!rs.next()) {
                    // no persons in DB.
                    return null;
                }
                Client[] clients = new DBClient[n];
                int i = 0;
                do {
                    clients[i++] = new DBClient(rs);
                } while (rs.next());
                if (i < n){
                    System.out.println("Not enough data in DB: only " + i + " persons, but required " + n);
                    return null;
                }
                return clients;
            }
        } catch (Exception e) {
            System.out.println("Could not connect to DB to get clients:");
            e.printStackTrace();
            return null;
        }
    }
    private static Connection getConnection() throws SQLException, IOException{
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("database.properties"))){
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }

    private static ApiClient[] requestClientsFromAPI(int n) throws IOException {
            ApiClient[] clients = new ApiClient[n];
        try {
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder uriBuilder = new URIBuilder(RANDOMUSER_ME_API);
            uriBuilder.setParameter("results", Integer.toString(n));
            uriBuilder.setParameter("inc", "name,dob,location,gender,nat");
            HttpGet request = new HttpGet(uriBuilder.build());
            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(result.toString());
            JSONArray arrResults = jsonObject.getJSONArray("results");
            ObjectMapper mapper = new ObjectMapper();
            Random r = new Random();
            for (int i = 0; i < arrResults.length(); i++) {
                JSONObject jsonClient = arrResults.getJSONObject(i);
                clients[i] = mapper.readValue(jsonClient.toString(), ApiClient.class);
                clients[i].inn = getRandInn(r);
                clients[i].flat = 1 + r.nextInt(200);
            }
        } catch (URISyntaxException uriEx) {
            System.out.println("Fatal error: Failed to build URI.");
        }
        return clients;
    }

    private static TxtGeneratedClient[] generateClientsFromFiles(Random r, int n) {
        ArrayList<String> cities = getArrayFromFilePath("src/main/resources/Cities.txt");
        ArrayList<String> countries = getArrayFromFilePath("src/main/resources/Countries.txt");
        ArrayList<String> manNames = getArrayFromFilePath("src/main/resources/ManNames.txt");
        ArrayList<String> manPatronymics = getArrayFromFilePath("src/main/resources/ManPatronymics.txt");
        ArrayList<String> manSurnames = getArrayFromFilePath("src/main/resources/ManSurnames.txt");
        ArrayList<String> regions = getArrayFromFilePath("src/main/resources/Regions.txt");
        ArrayList<String> streets = getArrayFromFilePath("src/main/resources/Streets.txt");
        ArrayList<String> womenNames = getArrayFromFilePath("src/main/resources/WomenNames.txt");
        ArrayList<String> womenPatronymics = getArrayFromFilePath("src/main/resources/WomenPatronymics.txt");
        ArrayList<String> womenSurnames = getArrayFromFilePath("src/main/resources/WomenSurnames.txt");

        TxtGeneratedClient[] clients = new TxtGeneratedClient[n];
        for (int i = 0; i < n; i++) {
            clients[i] = new TxtGeneratedClient();
            if (r.nextBoolean()) {
                clients[i].sex = "м";
                clients[i].name = manNames.get(r.nextInt(manNames.size()));
                clients[i].surname = manSurnames.get(r.nextInt(manSurnames.size()));
                clients[i].patronymic = manPatronymics.get(r.nextInt(manPatronymics.size()));
            } else {
                clients[i].sex = "ж";
                clients[i].name = womenNames.get(r.nextInt(manNames.size()));
                clients[i].surname = womenSurnames.get(r.nextInt(manSurnames.size()));
                clients[i].patronymic = womenPatronymics.get(r.nextInt(manPatronymics.size()));
            }

            clients[i].birthDay = getRandBD(r);
            clients[i].inn = getRandInn(r);
            clients[i].index = 100000 + r.nextInt(100000);
            clients[i].country = countries.get(r.nextInt(countries.size()));
            clients[i].region = regions.get(r.nextInt(regions.size()));
            clients[i].city = cities.get(r.nextInt(cities.size()));
            clients[i].street = streets.get(r.nextInt(streets.size()));
            clients[i].home = 1 + r.nextInt(100);
            clients[i].flat = 1 + r.nextInt(200);
        }
        return clients;
    }

    private static ArrayList<String> getArrayFromFilePath(String filePath) {
        ArrayList<String> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new InputStreamReader(new FileInputStream(filePath)));

            while (scanner.hasNext()) {
                result.add(scanner.nextLine());
            }
        } catch (IOException ex) {
            System.out.println("Could not read data due to " + ex.getMessage());
        }
        return result;
    }

    private static long getRandInn(Random r){
        int inspection = 1 + r.nextInt(36);
        int [] innDigits = {
                0,
                0,
                r.nextInt(10),
                r.nextInt(10),
                r.nextInt(10),
                r.nextInt(10),
                r.nextInt(10),
                r.nextInt(10),
                inspection % 10,
                inspection / 10,
                7,
                7};
        int [] const11 = {0, 0, 8, 6, 4, 9, 5, 3, 10, 4, 2, 7};
        int [] const12 = {0, 8, 6, 4, 9, 5, 3, 10, 4, 2, 7, 3};
        int sum = 0;
        for (int i=2; i<12; i++){
            sum += const11[i] * innDigits[i];
        }
        innDigits[1] = sum % 11 % 10;
        sum = 0;
        for (int i=1; i<12; i++){
            sum += const12[i] * innDigits[i];
        }
        innDigits[0] = sum % 11 % 10;
        long power = 1;
        long inn = 0;
        for (int i=0; i<12; i++, power *= 10){
            inn += innDigits[i] * power;
        }
        return inn;
    }

    private static LocalDate getRandBD(Random r){
        int year = 1930 + r.nextInt(80);
        int day = 1 + r.nextInt(LocalDate.of(year, 1, 1).lengthOfYear());
        return LocalDate.ofYearDay(year, day);
    }

    private static void generateExcel(Client[] clients) {
        try {
            Workbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Clients");
            Row titleRow = sheet.createRow(0);
            int colIx = 0;
            titleRow.createCell(colIx++).setCellValue("Имя");
            titleRow.createCell(colIx++).setCellValue("Фамилия");
            titleRow.createCell(colIx++).setCellValue("Отчество");
            titleRow.createCell(colIx++).setCellValue("Возраст");
            titleRow.createCell(colIx++).setCellValue("Пол");
            titleRow.createCell(colIx++).setCellValue("Дата рождения");
            titleRow.createCell(colIx++).setCellValue("ИНН");
            titleRow.createCell(colIx++).setCellValue("Индекс");
            titleRow.createCell(colIx++).setCellValue("Страна");
            titleRow.createCell(colIx++).setCellValue("Область");
            titleRow.createCell(colIx++).setCellValue("Город");
            titleRow.createCell(colIx++).setCellValue("Улица");
            titleRow.createCell(colIx++).setCellValue("Дом");
            titleRow.createCell(colIx).setCellValue("Кв.");

            DataFormat format = book.createDataFormat();
            CellStyle innStyle = book.createCellStyle();
            innStyle.setDataFormat(format.getFormat("#"));
            for (int rowIx = 1; rowIx <= clients.length; rowIx++) {
                Row row = sheet.createRow(rowIx);
                colIx = 0;
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getName());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getSurname());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getPatronomic());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getAge());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getGender());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getBD());
                Cell innCell = row.createCell(colIx++);
                innCell.setCellStyle(innStyle);
                innCell.setCellValue(clients[rowIx - 1].getInn());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getIndex());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getCountry());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getRegion());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getCity());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getStreet());
                row.createCell(colIx++).setCellValue(clients[rowIx - 1].getHouse());
                row.createCell(colIx).setCellValue(clients[rowIx - 1].getFlat());
            }
            for (colIx = 0; colIx < 14; colIx++) {
                sheet.autoSizeColumn(colIx);
            }
            book.write(new FileOutputStream(EXCEL_FILE_NAME));
            book.close();
            System.out.println(new File(EXCEL_FILE_NAME).getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not write Excel file.");
        }
    }

}
