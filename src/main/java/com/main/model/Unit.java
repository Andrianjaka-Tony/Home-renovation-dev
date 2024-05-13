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
public class Unit {

  private String id;
  private String name;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "UNIT", "_unit_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _unit";
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
    String sql = "INSERT INTO _unit (_id, _name) VALUES (?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _unit SET _name = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setString(2, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _unit WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static Unit createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    Unit unit = new Unit();

    unit.setId(resultSet.getString("_id"));
    unit.setName(resultSet.getString("_name"));

    return unit;
  }

  public static Unit findById(Connection connection, String id)
      throws SQLException {
    Unit response = null;
    String sql = "SELECT * FROM _unit WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Unit unit = createFromResultSet(connection, resultSet);
      response = unit;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Unit> findAll(Connection connection)
      throws SQLException {
    List<Unit> response = new ArrayList<>();
    String sql = "SELECT * FROM _unit";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Unit unit = createFromResultSet(connection, resultSet);
      response.add(unit);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Unit> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<Unit> response = new ArrayList<>();
    String sql = "SELECT * FROM _unit LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Unit unit = createFromResultSet(connection, resultSet);
      response.add(unit);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
