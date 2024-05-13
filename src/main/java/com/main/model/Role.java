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

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Role {

  private String id;
  private String name;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "ROLE", "_role_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _role";
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
    String sql = "INSERT INTO _role (_id, _name) VALUES (?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getName());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _role SET _name = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getName());
    statement.setString(2, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _role WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static Role createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    Role role = new Role();

    role.setId(resultSet.getString("_id"));
    role.setName(resultSet.getString("_name"));

    return role;
  }

  public static Role findById(Connection connection, String id)
      throws SQLException {
    Role response = null;
    String sql = "SELECT * FROM _v_main_role WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Role role = createFromResultSet(connection, resultSet);
      response = role;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Role> findAll(Connection connection)
      throws SQLException {
    List<Role> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_role";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Role role = createFromResultSet(connection, resultSet);
      response.add(role);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<Role> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<Role> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_role LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Role role = createFromResultSet(connection, resultSet);
      response.add(role);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
