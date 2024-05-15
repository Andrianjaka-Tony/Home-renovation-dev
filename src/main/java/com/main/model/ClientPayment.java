package com.main.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.main.helper.Connect;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPayment {

  private String id;
  private Double amount;
  private Date date;
  private ClientContract contract;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "CLIENT-PAYMENT", "_client_payment_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _client_payment";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getInt("_count");
    }
    resultSet.close();
    statement.close();
    return response;
  }

  public void save(Connection connection)
      throws SQLException {
    setId(nextId(connection));
    String sql = "INSERT INTO _client_payment (_id, _amount, _date, _contract) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setDouble(2, getAmount());
    statement.setDate(3, getDate());
    statement.setString(4, getContract().getId());
    statement.execute();
    statement.close();
  }

  public void saveFromCSV(Connection connection)
      throws SQLException {
    String sql = "INSERT INTO _client_payment (_id, _amount, _date, _contract) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setDouble(2, getAmount());
    statement.setDate(3, getDate());
    statement.setString(4, getContract().getId());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _client_payment SET _amount = ?, _date = ?, _contract = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setDouble(1, getAmount());
    statement.setDate(2, getDate());
    statement.setString(3, getContract().getId());
    statement.setString(4, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _client_payment WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static ClientPayment createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    ClientPayment clientpayment = new ClientPayment();

    clientpayment.setId(resultSet.getString("_id"));
    clientpayment.setAmount(resultSet.getDouble("_amount"));
    clientpayment.setDate(resultSet.getDate("_date"));

    ClientContract contract = new ClientContract();
    contract.setId(resultSet.getString("_contract_id"));
    contract.setBegin(resultSet.getTimestamp("_contract_begin"));
    contract.setEnd(resultSet.getTimestamp("_contract_end"));
    clientpayment.setContract(contract);

    return clientpayment;
  }

  public static ClientPayment findById(Connection connection, String id)
      throws SQLException {
    ClientPayment response = null;
    String sql = "SELECT * FROM _v_main_client_payment WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientPayment clientpayment = createFromResultSet(connection, resultSet);
      response = clientpayment;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientPayment> findAll(Connection connection)
      throws SQLException {
    List<ClientPayment> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_payment";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientPayment clientpayment = createFromResultSet(connection, resultSet);
      response.add(clientpayment);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientPayment> findAllByContract(Connection connection, ClientContract contract)
      throws SQLException {
    List<ClientPayment> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_payment WHERE _contract = ? ORDER BY _date ASC";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, contract.getId());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientPayment clientpayment = createFromResultSet(connection, resultSet);
      response.add(clientpayment);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  // public static Double totalPayment(Connection connection) throws SQLException
  // {
  // Double response = 0.0;
  // List<ClientPayment> payments = findAll(connection);
  // for (ClientPayment payment : payments) {
  // response += payment.amount;
  // }
  // return response;
  // }

  public static Double totalPayment(Connection connection)
      throws SQLException {
    Double response = null;
    String sql = "SELECT _amount FROM _v_client_payment_total";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getDouble("_amount");
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientPayment> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<ClientPayment> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_payment LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientPayment clientpayment = createFromResultSet(connection, resultSet);
      response.add(clientpayment);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
