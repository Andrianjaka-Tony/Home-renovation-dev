package com.main.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.Data;
import lombok.ToString;

import static com.main.helper.CsvHelper.parseDouble;

@Data
@ToString
public class HouseWork {

  private String house;
  private String description;
  private Double area;
  private String workId;
  private String workName;
  private String unitName;
  private Double unitPrice;
  private Double quantity;
  private Double duration;

  public static List<HouseWork> extract(String filePath) {
    List<HouseWork> response = new ArrayList<>();
    ClassPathResource res = new ClassPathResource("src/main/resources/static/" + filePath);
    File file = new File(res.getPath());
    try (CSVReader reader = new CSVReader(new FileReader(file))) {
      String[] line;
      reader.readNext();
      while ((line = reader.readNext()) != null) {
        HouseWork houseWork = new HouseWork();
        houseWork.setHouse(line[0]);
        houseWork.setDescription(line[1]);
        houseWork.setArea(parseDouble(line[2]));
        houseWork.setWorkId(line[3]);
        houseWork.setWorkName(line[4]);
        houseWork.setUnitName(line[5]);
        houseWork.setUnitPrice(parseDouble(line[6]));
        houseWork.setQuantity(parseDouble(line[7]));
        houseWork.setDuration(parseDouble(line[8]));
        response.add(houseWork);
      }
    } catch (IOException | CsvValidationException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  public void save(Connection connection)
      throws SQLException {
    String sql = "INSERT INTO _house_work_temp (_house, _description, _area, _work_id, _work_name, _unit_name, _unit_price, _quantity, _duration) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getHouse());
    statement.setString(2, getDescription());
    statement.setDouble(3, getArea());
    statement.setString(4, getWorkId());
    statement.setString(5, getWorkName());
    statement.setString(6, getUnitName());
    statement.setDouble(7, getUnitPrice());
    statement.setDouble(8, getQuantity());
    statement.setDouble(9, getDuration());
    statement.execute();
    statement.close();
  }

  public static void save(Connection connection, String filePath)
      throws SQLException {
    List<HouseWork> data = extract(filePath);
    for (HouseWork houseWork : data) {
      houseWork.save(connection);
    }
    saveUnits(connection);
    saveWorks(connection);
    saveHouses(connection);
    saveDetails(connection);
  }

  public static void saveUnits(Connection connection)
      throws SQLException {
    String sql = "SELECT _unit FROM _v_house_work_temp_unit_unsaved";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Unit unit = Unit.builder().name(resultSet.getString("_unit")).build();
      unit.save(connection);
    }
    statement.close();
    resultSet.close();
  }

  public static void saveWorks(Connection connection)
      throws SQLException {
    String sql = "SELECT * FROM _v_house_work_temp_work_to_save";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Work work = new Work();

      work.setId(resultSet.getString("_id"));
      work.setName(resultSet.getString("_name"));
      work.setPrice(resultSet.getDouble("_unit_price"));

      Unit unit = new Unit();
      unit.setId(resultSet.getString("_unit"));
      work.setUnit(unit);

      work.saveFromCSV(connection);
    }
    statement.close();
    resultSet.close();
  }

  public static void saveHouses(Connection connection)
      throws SQLException {
    String sql = "SELECT * FROM _v_house_work_temp_house_unsaved";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      House house = new House();
      house.setName(resultSet.getString("_house"));
      house.setDescription(resultSet.getString("_description"));
      house.setDuration(resultSet.getDouble("_duration"));
      house.setArea(resultSet.getDouble("_area"));

      house.save(connection);
    }
    statement.close();
    resultSet.close();
  }

  public static void saveDetails(Connection connection)
      throws SQLException {
    String sql = "SELECT * FROM _v_house_work_temp_house_details_unsaved";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      HouseDetails detail = new HouseDetails();
      detail.setQuantity(resultSet.getDouble("_quantity"));

      House house = House.builder().id(resultSet.getString("_house")).build();
      Work work = Work.builder().id(resultSet.getString("_work")).build();
      detail.setHouse(house);
      detail.setWork(work);

      detail.save(connection);
    }
    statement.close();
    resultSet.close();
  }

}
