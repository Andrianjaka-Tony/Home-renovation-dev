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
public class Work {

  private String id;
  private String name;
  private Double price;
  private Unit unit;
  private Work parent;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "WORK", "_work_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _work";
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
    String sql = "INSERT INTO _work (_id, _name, _price, _unit, _parent) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.setDouble(3, getPrice());
    statement.setString(4, getUnit().getId());
    statement.setString(5, getParent().getId());
    statement.execute();
    statement.close();
  }

  public void saveFromCSV(Connection connection)
      throws SQLException {
    String sql = "INSERT INTO _work (_id, _name, _price, _unit) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.setDouble(3, getPrice());
    statement.setString(4, getUnit().getId());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _work SET _name = ?, _price = ?, _unit = ?, _parent = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setDouble(2, getPrice());
    statement.setString(3, getUnit().getId());
    statement.setString(4, getParent().getId());
    statement.setString(5, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _work WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static Work createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    Work work = new Work();

    work.setId(resultSet.getString("_id"));
    work.setName(resultSet.getString("_name"));
    work.setPrice(resultSet.getDouble("_price"));

    Unit unit = new Unit();
    unit.setId(resultSet.getString("_unit_id"));
    unit.setName(resultSet.getString("_unit_name"));
    work.setUnit(unit);

    Work parent = new Work();
    parent.setId(resultSet.getString("_parent_id"));
    parent.setName(resultSet.getString("_parent_name"));
    work.setParent(parent);

    return work;
  }

  public static Work findById(Connection connection, String id)
      throws SQLException {
    Work response = null;
    String sql = "SELECT * FROM _v_main_work WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Work work = createFromResultSet(connection, resultSet);
      response = work;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Work> findAll(Connection connection)
      throws SQLException {
    List<Work> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_work";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Work work = createFromResultSet(connection, resultSet);
      response.add(work);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Work> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<Work> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_work LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Work work = createFromResultSet(connection, resultSet);
      response.add(work);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
