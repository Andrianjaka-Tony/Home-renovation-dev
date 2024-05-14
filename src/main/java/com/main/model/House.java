package com.main.model;

import java.sql.Connection;
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
public class House {

  private String id;
  private String name;
  private String description;
  private Double duration;
  private Double area;
  private Double price;
  private List<HouseDetails> details;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "HOUSE", "_house_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _house";
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
    String sql = "INSERT INTO _house (_id, _name, _description, _duration) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.setString(3, getDescription());
    statement.setDouble(4, getDuration());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _house SET _name = ?, _description = ?, _duration = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setString(2, getDescription());
    statement.setDouble(3, getDuration());
    statement.setString(4, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _house WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static House createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    House house = new House();

    house.setId(resultSet.getString("_id"));
    house.setName(resultSet.getString("_name"));
    house.setDescription(resultSet.getString("_description"));
    house.setDuration(resultSet.getDouble("_duration"));
    house.setPrice(resultSet.getDouble("_price"));
    house.setArea(resultSet.getDouble("_area"));

    return house;
  }

  public static House findById(Connection connection, String id)
      throws SQLException {
    House response = null;
    String sql = "SELECT * FROM _v_main_house WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      House house = createFromResultSet(connection, resultSet);
      response = house;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<House> findAll(Connection connection)
      throws SQLException {
    List<House> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_house";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      House house = createFromResultSet(connection, resultSet);
      response.add(house);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<House> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<House> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_house LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      House house = createFromResultSet(connection, resultSet);
      response.add(house);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public void updatePrixe(Connection connection)
      throws SQLException {
    setDetails(HouseDetails.findAllByHouse(connection, this));
    double response = 0;
    for (HouseDetails detail : details) {
      response += detail.getQuantity() * detail.getWork().getPrice();
    }
    setPrice(response);
  }

}
