package com.main.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvHelper {

  public static File createCsvFile(String content)
      throws IOException {
    File csvFile = new File("example.csv");
    FileWriter writer = new FileWriter(csvFile);
    writer.write(content);
    writer.close();
    return csvFile;
  }

}
