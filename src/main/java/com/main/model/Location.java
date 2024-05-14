package com.main.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.main.helper.Connect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

  private String id;
  private String name;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "LOCATION", "_location_sequence");
  }

  public void save(Connection connection)
      throws SQLException {
    setId(nextId(connection));
    String sql = "INSERT INTO _location (_id, _name) VALUES (?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.execute();
    statement.close();
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _location";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getInt("_count");
    }
    resultSet.close();
    statement.close();
    return response;
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _location SET _name = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setString(2, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _location WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static Location createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    Location location = new Location();

    location.setId(resultSet.getString("_id"));

    location.setName(resultSet.getString("_name"));

    return location;
  }

  public static Location findById(Connection connection, String id)
      throws SQLException {
    Location response = null;
    String sql = "SELECT * FROM _location WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Location location = createFromResultSet(connection, resultSet);
      response = location;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Location> findAll(Connection connection)
      throws SQLException {
    List<Location> response = new ArrayList<>();
    String sql = "SELECT * FROM _location";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Location location = createFromResultSet(connection, resultSet);
      response.add(location);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Location> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<Location> response = new ArrayList<>();
    String sql = "SELECT * FROM _location LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Location location = createFromResultSet(connection, resultSet);
      response.add(location);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
