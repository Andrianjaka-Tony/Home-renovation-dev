package com.main.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.main.helper.Connect;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientContract {

  private String id;
  private Timestamp begin;
  private Timestamp end;
  private Date date;
  private Client client;
  private House house;
  private FinishingType finishingType;
  private Double finishingAugmentation;
  private Double price;
  private Double payed;
  private List<ContractDetails> details;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "CLIENT-CONTRACT", "_client_contract_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _client_contract";
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
    setHouse(House.findById(connection, getHouse().getId()));
    setFinishingType(FinishingType.findById(connection, getFinishingType().getId()));
    setEnd(Connect.add(connection, getBegin(), getHouse().getDuration()));
    setDate(new Date(new java.util.Date(System.currentTimeMillis()).getTime()));

    String sql = "INSERT INTO _client_contract (_id, _begin, _end, _date, _client, _house, _finishing_type, _finishing_augmentation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setTimestamp(2, getBegin());
    statement.setTimestamp(3, getEnd());
    statement.setDate(4, getDate());
    statement.setString(5, getClient().getId());
    statement.setString(6, getHouse().getId());
    statement.setString(7, getFinishingType().getId());
    statement.setDouble(8, getFinishingType().getAugmentation());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _client_contract SET _begin = ?, _end = ?, _date = ?, _client = ?, _house = ?, _finishing_type = ?, _finishing_augmentation = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setTimestamp(1, getBegin());
    statement.setTimestamp(2, getEnd());
    statement.setDate(3, getDate());
    statement.setString(4, getClient().getId());
    statement.setString(5, getHouse().getId());
    statement.setString(6, getFinishingType().getId());
    statement.setDouble(7, getFinishingType().getAugmentation());
    statement.setString(8, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _client_contract WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static ClientContract createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    ClientContract clientcontract = new ClientContract();

    clientcontract.setId(resultSet.getString("_id"));
    clientcontract.setBegin(resultSet.getTimestamp("_begin"));
    clientcontract.setEnd(resultSet.getTimestamp("_end"));
    clientcontract.setDate(resultSet.getDate("_date"));
    clientcontract.setFinishingAugmentation(resultSet.getDouble("_finishing_augmentation"));
    clientcontract.setPrice(resultSet.getDouble("_price"));
    clientcontract.setPayed(resultSet.getDouble("_payed"));

    Client client = new Client();
    client.setId(resultSet.getString("_client_id"));
    client.setName(resultSet.getString("_client_name"));
    client.setContact(resultSet.getString("_client_contact"));
    clientcontract.setClient(client);

    House house = new House();
    house.setId(resultSet.getString("_house_id"));
    house.setName(resultSet.getString("_house_name"));
    house.setDuration(resultSet.getDouble("_house_duration"));
    clientcontract.setHouse(house);

    FinishingType finishingType = new FinishingType();
    finishingType.setId(resultSet.getString("_finishing_type_id"));
    finishingType.setName(resultSet.getString("_finishing_type_name"));
    finishingType.setAugmentation(resultSet.getDouble("_finishing_type_augmentation"));
    clientcontract.setFinishingType(finishingType);

    return clientcontract;
  }

  public static ClientContract findById(Connection connection, String id)
      throws SQLException {
    ClientContract response = null;
    String sql = "SELECT * FROM _v_main_client_contract WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract clientcontract = createFromResultSet(connection, resultSet);
      response = clientcontract;
    }
    statement.close();
    resultSet.close();
    response.setDetails(ContractDetails.findAllByContract(connection, response));
    return response;
  }

  public static List<ClientContract> findAll(Connection connection)
      throws SQLException {
    List<ClientContract> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_contract";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract clientcontract = createFromResultSet(connection, resultSet);
      response.add(clientcontract);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientContract> findAllCurrent(Connection connection)
      throws SQLException {
    List<ClientContract> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_current_client_contract";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract clientcontract = createFromResultSet(connection, resultSet);
      response.add(clientcontract);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientContract> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<ClientContract> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_contract LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract clientcontract = createFromResultSet(connection, resultSet);
      response.add(clientcontract);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ClientContract> findAllByClient(Connection connection, Client client)
      throws SQLException {
    List<ClientContract> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_client_contract WHERE _client_contact = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, client.getContact());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract clientcontract = createFromResultSet(connection, resultSet);
      response.add(clientcontract);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static Double totalPrice(Connection connection)
      throws SQLException {
    Double response = null;
    String sql = "SELECT _price FROM _v_client_contract_total_price";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getDouble("_price");
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Map<String, Object>> histogram(Connection connection)
      throws SQLException {
    List<Map<String, Object>> response = new ArrayList<>();
    String sql = "SELECT _month_year AS _date, _price FROM _v_client_contract_amount_month_year";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Map<String, Object> map = new HashMap<>();
      map.put("date", resultSet.getString("_date"));
      map.put("price", resultSet.getString("_price"));
      response.add(map);
    }
    resultSet.close();
    statement.close();
    return response;
  }

}
