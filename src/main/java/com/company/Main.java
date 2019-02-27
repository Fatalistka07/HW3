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
    public static final String PdfFileName = "Clients.pdf";

    public static void main(String[] args) throws IOException, URISyntaxException {
        Random r = new Random();
        int n = 1 + r.nextInt(30);
requestClients(n);
        Client[] clients = generateClients(r, n);

        //generateExcel(clients);

        //generatePdf(clients);
    }

    private static void requestClients(int n) throws URISyntaxException, IOException {
        HttpClient client = HttpClientBuilder.create().build();
        URIBuilder uriBuilder = new URIBuilder("https://randomuser.me/api/");
        uriBuilder.setParameter("results", Integer.toString(n));
        HttpGet request = new HttpGet(uriBuilder.build());
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        StringBuffer result = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result.toString());
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
            row.createCell(colIx++).setCellValue(clients[rowIx-1].name);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].surname);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].patronymic);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].age());
            row.createCell(colIx++).setCellValue(clients[rowIx-1].sex);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].birthDayStr());
            Cell innCell = row.createCell(colIx++);
            innCell.setCellStyle(innStyle);
            innCell.setCellValue(clients[rowIx-1].inn);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].index);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].country);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].region);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].city);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].street);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].home);
            row.createCell(colIx++).setCellValue(clients[rowIx-1].flat);
        }
        for (colIx = 0; colIx < 14; colIx++) {
            sheet.autoSizeColumn(colIx);
        }
        book.write(new FileOutputStream(ExcelFileName));
        book.close();
        System.out.println(new File(ExcelFileName).getAbsolutePath());
    }

    private static void generatePdf(Client[] clients) {
        String xmlStr = generateXml(clients);
        convertXmlToPdf(xmlStr);
        System.out.println(new File(PdfFileName).getAbsolutePath());
    }

    private static String generateXml(Client[] clients) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n\n    <fo:layout-master-set>\n        <fo:simple-page-master master-name=\"spm\"\n                               page-height=\"29.7cm\"\n                               page-width=\"21cm\"\n                               margin-top=\"1cm\"\n                               margin-bottom=\"1cm\"\n                               margin-left=\"1cm\"\n                               margin-right=\"1cm\">\n            <fo:region-body/>\n        </fo:simple-page-master>\n    </fo:layout-master-set>\n\n    <fo:page-sequence master-reference=\"spm\">\n        <fo:flow flow-name=\"xsl-region-body\">\n\n" +
                "<fo:table table-layout=\"fixed\" width=\"100%\" border-collapse=\"separate\">\n" +
                "   <fo:table-column column-width=\"12mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"7mm\"/>\n" +
                "   <fo:table-column column-width=\"4mm\"/>\n" +
                "   <fo:table-column column-width=\"15mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"10mm\"/>\n" +
                "   <fo:table-column column-width=\"15mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"19mm\"/>\n" +
                "   <fo:table-column column-width=\"5mm\"/>\n" +
                "   <fo:table-column column-width=\"5mm\"/>\n" +
                " <fo:table-body>");

        // table header
        sb.append("<fo:table-row>\n");
        xmlAddCell(sb, "Имя");
        xmlAddCell(sb, "Фамилия");
        xmlAddCell(sb, "Отчество");
        xmlAddCell(sb, "Возраст");
        xmlAddCell(sb, "Пол");
        xmlAddCell(sb, "Дата рождения");
        xmlAddCell(sb, "ИНН");
        xmlAddCell(sb, "Индекс");
        xmlAddCell(sb, "Страна");
        xmlAddCell(sb, "Область");
        xmlAddCell(sb, "Город");
        xmlAddCell(sb, "Улица");
        xmlAddCell(sb, "Дом");
        xmlAddCell(sb, "Кв.");
        sb.append("</fo:table-row>\n");

        for (int i = 0; i < clients.length; i++){
            sb.append("<fo:table-row>\n");
            xmlAddCell(sb, clients[i].name);
            xmlAddCell(sb, clients[i].surname);
            xmlAddCell(sb, clients[i].patronymic);
            xmlAddCell(sb, Integer.toString(clients[i].age()));
            xmlAddCell(sb, clients[i].sex);
            xmlAddCell(sb, clients[i].birthDayStr());
            xmlAddCell(sb, Long.toString(clients[i].inn));
            xmlAddCell(sb, Integer.toString(clients[i].index));
            xmlAddCell(sb, clients[i].country);
            xmlAddCell(sb, clients[i].region);
            xmlAddCell(sb, clients[i].city);
            xmlAddCell(sb, clients[i].street);
            xmlAddCell(sb, Integer.toString(clients[i].home));
            xmlAddCell(sb, Integer.toString(clients[i].flat));
            sb.append("</fo:table-row>\n");
        }
        sb.append("</fo:table-body>\n </fo:table>\n </fo:flow>\n </fo:page-sequence>\n </fo:root>");
        return sb.toString();
    }

    private static void xmlAddCell(StringBuffer sb, String cellValue) {
        sb.append("<fo:table-cell> <fo:block font-family=\"Arial\" font-size=\"6pt\"> \n");
        sb.append(cellValue);
        sb.append("</fo:block>\n </fo:table-cell>\n");
    }

    private static void convertXmlToPdf(String xmlStr){
        FopFactory fopFactory = FopFactory.newInstance();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Fop fop = fopFactory.newFop("application/pdf", new FileOutputStream(PdfFileName));
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new StreamSource(new ByteArrayInputStream(xmlStr.getBytes("UTF8")));
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(source, result);
        } catch (Exception var8) {
            System.err.println("Error of PDf generating: " + var8.getLocalizedMessage());
        }

    }
}
