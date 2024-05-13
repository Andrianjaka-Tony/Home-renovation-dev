package com.main.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

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

}
