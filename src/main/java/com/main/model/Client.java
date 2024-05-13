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

import com.main.exception.ClientException;
import com.main.helper.Connect;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

  private String id;
  private String name;
  private String contact;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "CLIENT", "_client_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _client";
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
    String sql = "INSERT INTO _client (_id, _name, _contact) VALUES (?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.setString(3, getContact());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _client SET _name = ?, _contact = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setString(2, getContact());
    statement.setString(3, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _client WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static Client createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    Client client = new Client();

    client.setId(resultSet.getString("_id"));
    client.setName(resultSet.getString("_name"));
    client.setContact(resultSet.getString("_contact"));

    return client;
  }

  public static Client findById(Connection connection, String id)
      throws SQLException {
    Client response = null;
    String sql = "SELECT * FROM _client WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Client client = createFromResultSet(connection, resultSet);
      response = client;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static Client findByContact(Connection connection, String contact)
      throws SQLException, ClientException {
    Client response = null;
    String sql = "SELECT * FROM _client WHERE _contact = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, contact);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Client client = createFromResultSet(connection, resultSet);
      response = client;
    }
    statement.close();
    resultSet.close();
    if (response == null) {
      throw new ClientException("Client with contact '" + contact + "' not found.");
    }
    return response;
  }

  public static List<Client> findAll(Connection connection)
      throws SQLException {
    List<Client> response = new ArrayList<>();
    String sql = "SELECT * FROM _client";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Client client = createFromResultSet(connection, resultSet);
      response.add(client);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Client> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<Client> response = new ArrayList<>();
    String sql = "SELECT * FROM _client LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Client client = createFromResultSet(connection, resultSet);
      response.add(client);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public void beginContract(Connection connection, ClientContract contract)
      throws SQLException, ClientException {
    setId(Client.findByContact(connection, getContact()).getId());
    contract.setClient(this);
    contract.save(connection);
    for (HouseDetails detail : HouseDetails.findAllByHouse(connection, contract.getHouse())) {
      ContractDetails contractDetails = new ContractDetails(null, detail.getQuantity(), detail.getWork().getPrice(),
          detail.getWork(), contract);
      contractDetails.save(connection);
    }
  }

  public void payment(Connection connection, ClientContract contract, Double amount, Date date)
      throws SQLException {
    ClientPayment payment = new ClientPayment();
    payment.setAmount(amount);
    payment.setDate(date);
    payment.setContract(contract);
    payment.save(connection);
  }

  public void payment(Connection connection, ClientPayment payment)
      throws SQLException {
    payment.save(connection);
  }

  public String signIn(Connection connection)
      throws SQLException, ClientException {
    if (Client.findByContact(connection, getContact()) == null) {
      Connect.startTransaction(connection);
      setName("Hello world");
      save(connection);
      connection.commit();
    }
    return getContact();
  }

}
