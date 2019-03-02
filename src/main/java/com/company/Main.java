package com.company;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
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


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class Main {

    public static final String ExcelFileName = "Clients.xls";

    public static void main(String[] args) throws IOException, URISyntaxException {
        Random r = new Random();
        int n = 1 + r.nextInt(30);
        Client[] clients;
        clients = requestClients(n);
        // clients = generateClients(r, n);

        generateExcel(clients);
    }

    private static Client[] requestClients(int n) throws URISyntaxException, IOException {
        HttpClient client = HttpClientBuilder.create().build();
        URIBuilder uriBuilder = new URIBuilder("https://randomuser.me/api/");
        uriBuilder.setParameter("results", Integer.toString(n));
        uriBuilder.setParameter("inc", "name,dob,location,gender,nat");
        HttpGet request = new HttpGet(uriBuilder.build());
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuffer result = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Client [] clients = new Client[n];
        JSONObject jsonObject = new JSONObject(result.toString());
        JSONArray arrResults = jsonObject.getJSONArray("results");
        ObjectMapper mapper = new ObjectMapper();
        Random r = new Random();
        for (int i = 0; i < arrResults.length(); i++)
        {
            JSONObject jsonClient = arrResults.getJSONObject(i);
            jsonClient.toString();
            clients[i] = mapper.readValue(jsonClient.toString(), Client.class);
            clients[i].inn = getRandInn(r);
            clients[i].flat = 1 + r.nextInt(200);
        }
        return clients;


    }

    private static Client[] generateClients(Random r, int n) throws IOException {
        ArrayList<String> cities = getArrayFromFilePath("./resources/Cities.txt");
        ArrayList<String> countries = getArrayFromFilePath("./resources/Countries.txt");
        ArrayList<String> manNames = getArrayFromFilePath("./resources/ManNames.txt");
        ArrayList<String> manPatronymics = getArrayFromFilePath("./resources/ManPatronymics.txt");
        ArrayList<String> manSurnames = getArrayFromFilePath("./resources/ManSurnames.txt");
        ArrayList<String> regions = getArrayFromFilePath("./resources/Regions.txt");
        ArrayList<String> streets = getArrayFromFilePath("./resources/Streets.txt");
        ArrayList<String> womenNames = getArrayFromFilePath("./resources/WomenNames.txt");
        ArrayList<String> womenPatronymics = getArrayFromFilePath("./resources/WomenPatronymics.txt");
        ArrayList<String> womenSurnames = getArrayFromFilePath("./resources/WomenSurnames.txt");

        Client [] clients = new Client[n];
        for (int i = 0; i < n; i++) {
            clients[i] = new Client();
            if (r.nextBoolean()) {
                clients[i].gender = "м";
                clients[i].name.first = manNames.get(r.nextInt(manNames.size()));
                clients[i].name.last = manSurnames.get(r.nextInt(manSurnames.size()));
                clients[i].name.title = manPatronymics.get(r.nextInt(manPatronymics.size()));
            } else {
                clients[i].gender = "ж";
                clients[i].name.first = womenNames.get(r.nextInt(manNames.size()));
                clients[i].name.last = womenSurnames.get(r.nextInt(manSurnames.size()));
                clients[i].name.title = womenPatronymics.get(r.nextInt(manPatronymics.size()));
            }

            clients[i].birthDay = getRandBD(r);
            clients[i].inn = getRandInn(r);
            clients[i].location.postcode = Integer.toString(100000 + r.nextInt(100000));
            clients[i].nat = countries.get(r.nextInt(countries.size()));
            clients[i].location.state = regions.get(r.nextInt(regions.size()));
            clients[i].location.city = cities.get(r.nextInt(cities.size()));
            clients[i].location.street = streets.get(r.nextInt(streets.size()));
            clients[i].home = 1 + r.nextInt(100);
            clients[i].flat = 1 + r.nextInt(200);
        }
        return clients;
    }

    private static ArrayList<String> getArrayFromFilePath(String filePath) throws IOException {
        FileInputStream fstream = new FileInputStream(filePath);
        Scanner scanner = new Scanner(new InputStreamReader(fstream));
        ArrayList<String> result = new ArrayList<String>();
        while ( scanner.hasNext()) {
            result.add(scanner.nextLine());
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

    private static void generateExcel(Client[] clients) throws IOException {
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
        titleRow.createCell(colIx++).setCellValue("Кв.");

        DataFormat format = book.createDataFormat();
        CellStyle innStyle = book.createCellStyle();
        innStyle.setDataFormat(format.getFormat("#"));
        for (int rowIx = 1; rowIx <= clients.length; rowIx++) {
            Row row = sheet.createRow(rowIx);
            colIx = 0;
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getName());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getSurname());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getPatronomic());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getAge());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getGender());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].gatBD());
            Cell innCell = row.createCell(colIx++);
            innCell.setCellStyle(innStyle);
            innCell.setCellValue(clients[rowIx-1].inn);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getIndex());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getCountry());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getRegion());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getCity());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getStreet());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].getHouse());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].flat);
        }
        for (colIx = 0; colIx < 14; colIx++) {
            sheet.autoSizeColumn(colIx);
        }
        book.write(new FileOutputStream(ExcelFileName));
        book.close();
        System.out.println(new File(ExcelFileName).getAbsolutePath());
    }

}
