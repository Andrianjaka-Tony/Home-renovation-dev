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

import com.main.exception.ClientException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.Data;
import lombok.ToString;

import static com.main.helper.CsvHelper.parseTimeStamp;
import static com.main.helper.CsvHelper.parseDate;
import static com.main.helper.CsvHelper.parseDouble;

@Data
@ToString
public class ContractCSV {

  private String client;
  private String id;
  private String house;
  private String finishingName;
  private String augmentation;
  private String date;
  private String begin;
  private String location;

  public static List<ContractCSV> extract(String filePath) {
    List<ContractCSV> response = new ArrayList<>();
    ClassPathResource res = new ClassPathResource("src/main/resources/static/" + filePath);
    File file = new File(res.getPath());
    try (CSVReader reader = new CSVReader(new FileReader(file))) {
      String[] line;
      reader.readNext();
      while ((line = reader.readNext()) != null) {
        ContractCSV contract = new ContractCSV();
        contract.setClient(line[0]);
        contract.setId(line[1]);
        contract.setHouse(line[2]);
        contract.setFinishingName(line[3]);
        contract.setAugmentation("" + parseDouble(line[4].substring(0, line[4].length() - 1)));
        contract.setDate(parseDate(line[5]));
        contract.setBegin(parseTimeStamp(line[6]));
        contract.setLocation(line[7]);
        response.add(contract);
      }
    } catch (IOException | CsvValidationException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  public void save(Connection connection)
      throws SQLException {
    String sql = "INSERT INTO _contract_temp (_client, _id, _house, _finishing_name, _augmentation, _date, _begin, _location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getClient());
    statement.setString(2, getId());
    statement.setString(3, getHouse());
    statement.setString(4, getFinishingName());
    statement.setString(5, getAugmentation());
    statement.setString(6, getDate());
    statement.setString(7, getBegin());
    statement.setString(8, getLocation());
    statement.execute();
    statement.close();
  }

  public static void save(Connection connection, String filePath)
      throws SQLException, ClientException {
    List<ContractCSV> data = extract(filePath);
    for (ContractCSV row : data) {
      row.save(connection);
      Client client = Client.builder().contact(row.getClient()).build();
      client.signIn(connection);
    }
    saveLocations(connection);
    saveContracts(connection);
  }

  public static void saveLocations(Connection connection)
      throws SQLException {
    String sql = "SELECT _name FROM _v_contract_temp_location_unsaved";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      Location location = Location.builder().name(resultSet.getString("_name")).build();
      location.save(connection);
    }
    statement.close();
    resultSet.close();
  }

  public static void saveContracts(Connection connection)
      throws SQLException, ClientException {
    String sql = "SELECT * FROM _v_contract_temp_contract_unsaved";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientContract contract = new ClientContract();

      contract.setId(resultSet.getString("_id"));
      contract.setBegin(resultSet.getTimestamp("_begin"));

      Client client = Client.findById(connection, resultSet.getString("_client"));
      contract.setClient(client);

      House house = House.builder().id(resultSet.getString("_house")).build();
      contract.setHouse(house);

      FinishingType finishingType = FinishingType.builder().id(resultSet.getString("_finishing_type")).build();
      contract.setFinishingType(finishingType);

      contract.setDate(resultSet.getDate("_date"));
      contract.setFinishingAugmentation(resultSet.getDouble("_augmentation"));

      Location location = Location.builder().id(resultSet.getString("_location")).build();
      contract.setLocation(location);

      client.beginContractCSV(connection, contract);
    }
  }

}
