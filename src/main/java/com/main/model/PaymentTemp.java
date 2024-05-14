package com.main.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.main.helper.Connect;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.Data;
import lombok.ToString;

import static com.main.helper.CsvHelper.parseDate;
import static com.main.helper.CsvHelper.parseDouble;

@Data
@ToString
public class PaymentTemp {

  private String contract;
  private String payment;
  private String date;
  private Double amount;

  public static List<PaymentTemp> extract(String filePath) {
    List<PaymentTemp> response = new ArrayList<>();
    ClassPathResource res = new ClassPathResource("src/main/resources/static/" + filePath);
    File file = new File(res.getPath());
    try (CSVReader reader = new CSVReader(new FileReader(file))) {
      String[] line;
      reader.readNext();
      while ((line = reader.readNext()) != null) {
        PaymentTemp payment = new PaymentTemp();
        payment.setContract(line[0]);
        payment.setPayment(line[1]);
        payment.setDate(parseDate(line[2]));
        payment.setAmount(parseDouble(line[3]));
        response.add(payment);
      }
    } catch (IOException | CsvValidationException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  public void save(Connection connection)
      throws SQLException {
    String sql = "INSERT INTO _payment_temp (_contract, _payment, _date, _amount) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getContract());
    statement.setString(2, getPayment());
    statement.setString(3, getDate());
    statement.setString(4, "" + getAmount());
    statement.execute();
    statement.close();
  }

  public static void savePayments(Connection connection)
      throws SQLException {
    String sql = "SELECT * FROM _payment_temp";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientPayment payment = new ClientPayment();

      payment.setId(resultSet.getString("_payment"));
      payment.setDate(resultSet.getDate("_date"));
      payment.setAmount(resultSet.getDouble("_amount"));

      ClientContract contract = ClientContract.builder().id(resultSet.getString("_contract")).build();
      payment.setContract(contract);

      payment.saveFromCSV(connection);
    }
    statement.close();
    resultSet.close();
  }

  public void savePayment(Connection connection)
      throws SQLException {
    ClientPayment payment = ClientPayment.builder()
        .id(getPayment())
        .contract(ClientContract.findById(connection, getContract()))
        .date(Connect.convertToSqlDate(getDate()))
        .amount(getAmount())
        .build();
    payment.saveFromCSV(connection);
  }

  public static void save(Connection connection, String filePath)
      throws SQLException {
    List<PaymentTemp> paymentTemps = extract(filePath);
    for (PaymentTemp payment : paymentTemps) {
      // payment.save(connection);
      payment.savePayment(connection);
    }
    // savePayments(connection);
  }

}
