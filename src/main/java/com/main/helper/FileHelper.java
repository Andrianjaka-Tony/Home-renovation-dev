package com.main.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileHelper {

  public static String encodeToBase64(String content) {
    return Base64.getEncoder().encodeToString(content.getBytes());
  }

  public static String encodeToBase64(File file)
      throws IOException {
    byte[] fileContent = Files.readAllBytes(file.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
  }

  public static List<String> readFile(File file) {
    try {
      return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void upload(String base64, String path) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64);
    File file = new File("src/main/resources/static/" + path);
    try {
      file.createNewFile();
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(decodedBytes);
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static File excel(String fileName, List<String[]> data, String[] columns) {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Sheet1");

    // Create a Font for styling header cells
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short) 12);
    headerFont.setColor(IndexedColors.BLACK.getIndex());

    // Create a CellStyle with the font
    CellStyle headerCellStyle = workbook.createCellStyle();
    headerCellStyle.setFont(headerFont);

    // Create the header row
    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < columns.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(columns[i]);
      cell.setCellStyle(headerCellStyle);
    }

    // Create other rows and cells with data
    int rowNum = 1;
    for (String[] rowData : data) {
      Row row = sheet.createRow(rowNum++);
      for (int i = 0; i < rowData.length; i++) {
        row.createCell(i).setCellValue(rowData[i]);
      }
    }

    // Resize all columns to fit the content size
    for (int i = 0; i < columns.length; i++) {
      sheet.autoSizeColumn(i);
    }

    // Write the output to a file
    File file = new File(fileName);
    try (FileOutputStream fileOut = new FileOutputStream(file)) {
      workbook.write(fileOut);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Closing the workbook
    try {
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return file;
  }

  public static List<String[]> getDataFromExcel(File file) {
    List<String[]> data = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis)) {

      Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
      for (Row row : sheet) {
        List<String> rowData = new ArrayList<>();
        for (Cell cell : row) {
          switch (cell.getCellType()) {
            case STRING:
              rowData.add(cell.getStringCellValue());
              break;
            case NUMERIC:
              if (DateUtil.isCellDateFormatted(cell)) {
                rowData.add(cell.getDateCellValue().toString());
              } else {
                rowData.add(String.valueOf(cell.getNumericCellValue()));
              }
              break;
            case BOOLEAN:
              rowData.add(String.valueOf(cell.getBooleanCellValue()));
              break;
            case FORMULA:
              rowData.add(cell.getCellFormula());
              break;
            case BLANK:
              rowData.add("");
              break;
            default:
              rowData.add("");
          }
        }
        data.add(rowData.toArray(new String[0]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }

}
