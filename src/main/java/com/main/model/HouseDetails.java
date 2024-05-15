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
public class HouseDetails {

  private String id;
  private Double quantity;
  private House house;
  private Work work;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "HOUSE-DETAILS", "_house_details_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _house_details";
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
    String sql = "INSERT INTO _house_details (_id, _quantity, _house, _work) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setDouble(2, getQuantity());
    statement.setString(3, getHouse().getId());
    statement.setString(4, getWork().getId());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _house_details SET _quantity = ?, _house = ?, _work = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setDouble(1, getQuantity());
    statement.setString(2, getHouse().getId());
    statement.setString(3, getWork().getId());
    statement.setString(4, getId());
    statement.execute();
    statement.close();
  }

  public void updateQuantity(Connection connection)
      throws SQLException {
    String sql = "UPDATE _house_details SET _quantity = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setDouble(1, getQuantity());
    statement.setString(2, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _house_details WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static HouseDetails createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    HouseDetails housedetails = new HouseDetails();

    housedetails.setId(resultSet.getString("_id"));
    housedetails.setQuantity(resultSet.getDouble("_quantity"));

    House house = new House();
    house.setId(resultSet.getString("_house_id"));
    house.setName(resultSet.getString("_house_name"));
    house.setDuration(resultSet.getDouble("_house_duration"));
    housedetails.setHouse(house);

    Work work = new Work();
    work.setId(resultSet.getString("_work_id"));
    work.setName(resultSet.getString("_work_name"));
    work.setPrice(resultSet.getDouble("_work_price"));
    housedetails.setWork(work);

    return housedetails;
  }

  public static HouseDetails findById(Connection connection, String id)
      throws SQLException {
    HouseDetails response = null;
    String sql = "SELECT * FROM _v_main_house_details WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails housedetails = createFromResultSet(connection, resultSet);
      response = housedetails;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static HouseDetails findByHouseAndWork(Connection connection, House house, Work work)
      throws SQLException {
    HouseDetails response = null;
    String sql = "SELECT * FROM _v_main_house_details WHERE _house = ? AND _work = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, house.getId());
    statement.setString(2, work.getId());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails housedetails = createFromResultSet(connection, resultSet);
      response = housedetails;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<HouseDetails> findAll(Connection connection)
      throws SQLException {
    List<HouseDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_house_details";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails housedetails = createFromResultSet(connection, resultSet);
      response.add(housedetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<HouseDetails> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<HouseDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_house_details LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails housedetails = createFromResultSet(connection, resultSet);
      response.add(housedetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  // house
  public static int countByHouse(Connection connection, House house)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _house_details WHERE _house = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, house.getId());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      response = resultSet.getInt("_count");
    }
    resultSet.close();
    statement.close();
    return response;
  }

  public static List<HouseDetails> findAllByHouse(Connection connection, House house)
      throws SQLException {
    List<HouseDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_house_details WHERE _house = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, house.getId());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails housedetails = createFromResultSet(connection, resultSet);
      response.add(housedetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
