package com.main.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvHelper {

  public static File createCsvFile(String content)
      throws IOException {
    File csvFile = new File("example.csv");
    FileWriter writer = new FileWriter(csvFile);
    writer.write(content);
    writer.close();
    return csvFile;
  }

  public static Double parseDouble(String value) {
    value = value.replace(",", ".");
    return Double.parseDouble(value);
  }

  public static String parseDate(String input) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      Date date = dateFormat.parse(input);
      SimpleDateFormat responseFormat = new SimpleDateFormat("yyyy-MM-dd");
      return responseFormat.format(date);
    } catch (Exception e) {
      return null;
    }
  }

  public static String parseTimeStamp(String input) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      Date date = dateFormat.parse(input);
      SimpleDateFormat responseFormat = new SimpleDateFormat("yyyy-MM-dd 08:00:00");
      return responseFormat.format(date);
    } catch (Exception e) {
      return null;
    }
  }

}
