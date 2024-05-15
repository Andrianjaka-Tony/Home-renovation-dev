package com.main.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.main.exception.ClientException;
import com.main.helper.Connect;
import com.main.helper.FileHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportData {

  private String houseWork;
  private String contract;
  private String payment;

  public void process(Connection connection)
      throws SQLException, ClientException {
    importHouseWork(connection);
    importContract(connection);
  }

  private void importHouseWork(Connection connection)
      throws SQLException {
    setHouseWork(getHouseWork().split(",")[1]);
    String filename = "csv/" + System.currentTimeMillis() + "-house-work.csv";
    FileHelper.upload(getHouseWork(), filename);
    HouseWork.save(connection, filename);
  }

  private void importContract(Connection connection)
      throws SQLException, ClientException {
    setContract(getContract().split(",")[1]);
    String filename = "csv/" + System.currentTimeMillis() + "-contract.csv";
    FileHelper.upload(getContract(), filename);
    ContractCSV.save(connection, filename);
  }

  public void importPayment(Connection connection)
      throws SQLException, ClientException {
    setPayment(getPayment().split(",")[1]);
    String filename = "csv/" + System.currentTimeMillis() + "-payment.csv";
    FileHelper.upload(getPayment(), filename);
    PaymentTemp.save(connection, filename);
  }

  public void deleteTempDatas(Connection connection)
      throws SQLException {
    Connect.startTransaction(connection);
    List<String> queries = List.of(
        "DELETE FROM _house_work_temp",
        "DELETE FROM _contract_temp",
        "DELETE FROM _payment_temp");
    for (String query : queries) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.execute();
      statement.close();
    }
    connection.commit();
  }

}
