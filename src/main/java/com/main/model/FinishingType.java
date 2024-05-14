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
public class FinishingType {

  private String id;
  private String name;
  private Double augmentation;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "FINISH-TYPE", "_finishing_type_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _finishing_type";
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
    String sql = "INSERT INTO _finishing_type (_id, _name, _augmentation) VALUES (?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.setDouble(3, getAugmentation());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _finishing_type SET _name = ?, _augmentation = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setDouble(2, getAugmentation());
    statement.setString(3, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _finishing_type WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static FinishingType createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    FinishingType finishingtype = new FinishingType();

    finishingtype.setId(resultSet.getString("_id"));
    finishingtype.setName(resultSet.getString("_name"));
    finishingtype.setAugmentation(resultSet.getDouble("_augmentation"));

    return finishingtype;
  }

  public static FinishingType findById(Connection connection, String id)
      throws SQLException {
    FinishingType response = null;
    String sql = "SELECT * FROM _finishing_type WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      FinishingType finishingtype = createFromResultSet(connection, resultSet);
      response = finishingtype;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static FinishingType findByName(Connection connection, String name)
      throws SQLException {
    FinishingType response = null;
    String sql = "SELECT * FROM _finishing_type WHERE _name = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, name);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      FinishingType finishingtype = createFromResultSet(connection, resultSet);
      response = finishingtype;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<FinishingType> findAll(Connection connection)
      throws SQLException {
    List<FinishingType> response = new ArrayList<>();
    String sql = "SELECT * FROM _finishing_type";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      FinishingType finishingtype = createFromResultSet(connection, resultSet);
      response.add(finishingtype);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<FinishingType> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<FinishingType> response = new ArrayList<>();
    String sql = "SELECT * FROM _finishing_type LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      FinishingType finishingtype = createFromResultSet(connection, resultSet);
      response.add(finishingtype);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
