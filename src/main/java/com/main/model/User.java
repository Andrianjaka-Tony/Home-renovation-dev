package com.main.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.main.exception.SignInException;
import com.main.exception.TokenException;
import com.main.exception.UserException;
import com.main.helper.Connect;
import com.main.helper.PasswordHelper;
import com.main.helper.TokenHelper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Role role;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "USER", "_user_sequence");
  }

  public static User findByEmail(Connection connection, String email)
      throws SQLException {
    User response = null;
    String sql = "SELECT * FROM _v_main_user WHERE _email = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, email);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = User.builder()
          .id(resultSet.getString("_id"))
          .firstName(resultSet.getString("_first_name"))
          .lastName(resultSet.getString("_last_name"))
          .email(email)
          .password(resultSet.getString("_password"))
          .build();

      Role role = Role.builder()
          .id(resultSet.getString("_role_id"))
          .name(resultSet.getString("_role_name"))
          .build();

      response.setRole(role);
    }
    resultSet.close();
    statement.close();
    return response;
  }

  public static User findByToken(Connection connection, String token)
      throws SQLException, TokenException {
    String email = TokenHelper.extractEmailFromToken(token);
    if (!TokenHelper.isValidToken(token)) {
      throw new TokenException("The token is expired.");
    }
    return User.findByEmail(connection, email);
  }

  public String signIn(Connection connection)
      throws SQLException, SignInException {
    User user = User.findByEmail(connection, getEmail());
    if (user == null) {
      throw new SignInException("The user with email '" + email + "' dows not exist.");
    }
    if (!PasswordHelper.checkPassword(getPassword(), user.getPassword())) {
      throw new SignInException("Wrong password.");
    }
    setRole(user.getRole());
    return TokenHelper.generateTokenFromEmail(email);
  }

  public void signUp(Connection connection)
      throws SQLException {
    setId(nextId(connection));
    String sql = "INSERT INTO _user (_id, _first_name, _last_name, _email, _password, _role) VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getFirstName());
    statement.setString(3, getLastName());
    statement.setString(4, getEmail());
    statement.setString(5, PasswordHelper.encodePassword(getPassword()));
    statement.setString(6, getRole().getId());
    statement.execute();
    statement.close();
  }

  public void checkPermission(List<Role> roles)
      throws UserException {
    boolean includes = false;
    for (Role role : roles) {
      if (role.getId().equals(getRole().getId())) {
        includes = true;
      }
    }
    if (!includes) {
      throw new UserException("You don't have the permission to do this action.");
    }
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _user";
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
    String sql = "INSERT INTO _user (_id, _first_name, _last_name, _email, _password, _role) VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setString(2, getFirstName());
    statement.setString(3, getLastName());
    statement.setString(4, getEmail());
    statement.setString(5, getPassword());
    statement.setString(6, getRole().getId());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _user SET _first_name = ?, _last_name = ?, _email = ?, _password = ?, _role = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getFirstName());
    statement.setString(2, getLastName());
    statement.setString(3, getEmail());
    statement.setString(4, getPassword());
    statement.setString(5, getRole().getId());
    statement.setString(6, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _user WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static User createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    User user = new User();

    user.setId(resultSet.getString("_id"));
    user.setFirstName(resultSet.getString("_first_name"));
    user.setLastName(resultSet.getString("_last_name"));
    user.setEmail(resultSet.getString("_email"));
    user.setPassword(resultSet.getString("_password"));

    Role role = new Role();
    role.setId(resultSet.getString("_role_id"));
    role.setName(resultSet.getString("_role_name"));
    user.setRole(role);

    return user;
  }

  public static User findById(Connection connection, String id)
      throws SQLException {
    User response = null;
    String sql = "SELECT * FROM _v_main_user WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      User user = createFromResultSet(connection, resultSet);
      response = user;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<User> findAll(Connection connection)
      throws SQLException {
    List<User> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_user";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      User user = createFromResultSet(connection, resultSet);
      response.add(user);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<User> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<User> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_user LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      User user = createFromResultSet(connection, resultSet);
      response.add(user);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
