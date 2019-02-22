package com.company;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
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
        //titleRow.createCell(colIx++).setCellValue("Возраст");
        titleRow.createCell(colIx++).setCellValue("Пол");
        //titleRow.createCell(colIx++).setCellValue("Дата рождения");
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

            colIx = 0;
            if (isMan) {
                row.createCell(colIx++).setCellValue(manNames.get(r.nextInt(manNames.size())));
                row.createCell(colIx++).setCellValue(manSurnames.get(r.nextInt(manSurnames.size())));
                row.createCell(colIx++).setCellValue(manPatronymics.get(r.nextInt(manPatronymics.size())));
                row.createCell(colIx++).setCellValue("М");
            } else {
                row.createCell(colIx++).setCellValue(womenNames.get(r.nextInt(womenNames.size())));
                row.createCell(colIx++).setCellValue(womenSurnames.get(r.nextInt(womenSurnames.size())));
                row.createCell(colIx++).setCellValue(womenPatronymics.get(r.nextInt(womenPatronymics.size())));
                row.createCell(colIx++).setCellValue("Ж");
            }
            // bd
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
        book.write(new FileOutputStream("Clients.xls"));
        book.close();
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
}
