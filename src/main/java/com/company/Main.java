package com.company;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static final String ExcelFileName = "Clients.xls";
    public static final String PdfFileName = "Clients.pdf";

    public static void main(String[] args) throws IOException, URISyntaxException {
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

        Random r = new Random();
        int n = 1 + r.nextInt(30);
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
        titleRow.createCell(colIx++).setCellValue("Почтовый индекс");
        titleRow.createCell(colIx++).setCellValue("Страна");
        titleRow.createCell(colIx++).setCellValue("Область");
        titleRow.createCell(colIx++).setCellValue("Город");
        titleRow.createCell(colIx++).setCellValue("Улица");
        titleRow.createCell(colIx++).setCellValue("Дом");
        titleRow.createCell(colIx++).setCellValue("Квартира");

        for (int rowIx = 1; rowIx <= n; rowIx++) {
            Row row = sheet.createRow(rowIx);

            boolean isMan = r.nextBoolean();

            LocalDate bd = getRandBD(r);

            colIx = 0;
            if (isMan) {
                row.createCell(colIx++).setCellValue(manNames.get(r.nextInt(manNames.size())));
                row.createCell(colIx++).setCellValue(manSurnames.get(r.nextInt(manSurnames.size())));
                row.createCell(colIx++).setCellValue(manPatronymics.get(r.nextInt(manPatronymics.size())));
            } else {
                row.createCell(colIx++).setCellValue(womenNames.get(r.nextInt(womenNames.size())));
                row.createCell(colIx++).setCellValue(womenSurnames.get(r.nextInt(womenSurnames.size())));
                row.createCell(colIx++).setCellValue(womenPatronymics.get(r.nextInt(womenPatronymics.size())));
            }
            row.createCell(colIx++).setCellValue(Period.between(bd,
                    LocalDate.now()).getYears());
            row.createCell(colIx++).setCellValue(isMan ? "М" : "Ж");
            row.createCell(colIx++).setCellValue(bd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            row.createCell(colIx++).setCellValue(getRandInn(r));
            row.createCell(colIx++).setCellValue(100000 + r.nextInt(100000));
            row.createCell(colIx++).setCellValue(countries.get(r.nextInt(countries.size())));
            row.createCell(colIx++).setCellValue(regions.get(r.nextInt(regions.size())));
            row.createCell(colIx++).setCellValue(cities.get(r.nextInt(cities.size())));
            row.createCell(colIx++).setCellValue(streets.get(r.nextInt(streets.size())));
            row.createCell(colIx++).setCellValue(1 + r.nextInt(100));
            row.createCell(colIx++).setCellValue(1 + r.nextInt(200));


        }
        for (colIx = 0; colIx < 14; colIx++) {
            sheet.autoSizeColumn(colIx);
        }
        book.write(new FileOutputStream(ExcelFileName));
        book.close();
        System.out.println(new File(ExcelFileName).getAbsolutePath());
        String xmlStr = convertExcelToXmlString(sheet);
        convertXmlToString(xmlStr);
        System.out.println(new File(PdfFileName).getAbsolutePath());
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
    private static String convertExcelToXmlString(Sheet sheet) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n\n    <fo:layout-master-set>\n        <fo:simple-page-master master-name=\"spm\"\n                               page-height=\"29.7cm\"\n                               page-width=\"21cm\"\n                               margin-top=\"1cm\"\n                               margin-bottom=\"1cm\"\n                               margin-left=\"1cm\"\n                               margin-right=\"1cm\">\n            <fo:region-body/>\n        </fo:simple-page-master>\n    </fo:layout-master-set>\n\n    <fo:page-sequence master-reference=\"spm\">\n        <fo:flow flow-name=\"xsl-region-body\">\n\n            <fo:block font-weight=\"bold\" font-size=\"16pt\" font-family=\"sans-serif\" line-height=\"24pt\"\n                      space-after.optimum=\"15pt\" text-align=\"center\" padding-top=\"3pt\">\n                Credit card processing statement\n            </fo:block>\n\n" +
                "<fo:table table-layout=\"fixed\" width=\"100%\" border-collapse=\"separate\">\n   <fo:table-column column-width=\"19mm\"/>\n   <fo:table-column column-width=\"19\"/>\n                <fo:table-column column-width=\"19mm\"/>\n                <fo:table-column column-width=\"19mm\"/>\n  <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n <fo:table-column column-width=\"19mm\"/>\n  <fo:table-body>");

        for (int rowIx = sheet.getFirstRowNum();rowIx < sheet.getLastRowNum(); rowIx++){
            Row row = sheet.getRow(rowIx);
            sb.append("<fo:table-row>\n");
            for (int colIx = row.getFirstCellNum(); colIx < row.getLastCellNum(); colIx++){
                Cell cell = row.getCell(colIx);
                sb.append("<fo:table-cell> <fo:block font-size=\"6pt\">");
                //font-family=\"serif\">\n"
                switch (cell.getCellType())
                {
                    case 1: sb.append(cell.getStringCellValue()); break;
                    case 0: sb.append(cell.getNumericCellValue()); break;
                }

                sb.append("</fo:block>\n </fo:table-cell>\n");
            }
            sb.append("</fo:table-row>\n");
        }
        sb.append("</fo:table-body>\n </fo:table>\n </fo:flow>\n </fo:page-sequence>\n </fo:root>");
        return sb.toString();
    }
    private static void convertXmlToString(String xmlStr){
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
