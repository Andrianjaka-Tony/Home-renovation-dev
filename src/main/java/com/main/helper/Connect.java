package com.main.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Connect {

  public static Connection getConnection() {
    Connection con = null;
    try {
      Class.forName("org.postgresql.Driver");
      con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/_home_renovation", "postgres", "root");
      con.setAutoCommit(false);
    } catch (Exception e) {
      System.out.println(e);
    }
    return con;
  }

  public static String createSuffix(int value) {
    String response = value + "";
    while (response.length() != 9) {
      response = "0" + response;
    }
    return response;
  }

  public static String nextId(Connection connection, String prefix, String sequence)
      throws SQLException {
    int value = 0;
    String sql = "SELECT NEXTVAL('" + sequence + "') AS value";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      value = resultSet.getInt("value");
    }
    resultSet.close();
    statement.close();
    return prefix + createSuffix(value);
  }

  public static void startTransaction(Connection connection)
      throws SQLException {
    String sql = "START TRANSACTION";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.execute();
    statement.close();
  }

  public static void resetDatabase(Connection connection)
      throws SQLException {
    List<String> queries = List.of("DELETE FROM _user WHERE _role = 'ROLE02'", "DELETE FROM _house_work_temp",
        "DELETE FROM _contract_temp", "DELETE FROM _payment_temp", "DELETE FROM _contract_details",
        "DELETE FROM _client_payment",
        "DELETE FROM _client_contract", "DELETE FROM _house_details", "DELETE FROM _work", "DELETE FROM _house",
        "DELETE FROM _client", "DELETE FROM _unit", "DELETE FROM _location");
    for (String query : queries) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.execute();
      statement.close();
    }
  }

  public static void close(Connection connection) {
    try {
      connection.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static void rollback(Connection connection) {
    try {
      connection.rollback();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static Timestamp add(Connection connection, Timestamp begin, Double days)
      throws SQLException {
    Timestamp response = null;
    String sql = "SELECT TIMESTAMP '" + begin.toString() + "' + INTERVAL '" + days + " days' AS _timestamp";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getTimestamp("_timestamp");
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static Date convertToSqlDate(String date) {
    try {
      SimpleDateFormat responseFormat = new SimpleDateFormat("yyyy-MM-dd");
      java.util.Date parsedDate = responseFormat.parse(date);
      return new Date(parsedDate.getTime());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Timestamp convertToSqlTimestamp(String timestamp) {
    try {
      SimpleDateFormat responseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      java.util.Date parsedDate = responseFormat.parse(timestamp);
      return new Timestamp(parsedDate.getTime());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
