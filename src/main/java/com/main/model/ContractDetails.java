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
public class ContractDetails {

  private String id;
  private Double quantity;
  private Double unitPrice;
  private Work work;
  private ClientContract contract;
  private Unit unit;

  public String nextId(Connection connection)
      throws SQLException {
    return Connect.nextId(connection, "CONTRACT-DETAILS", "_contract_details_sequence");
  }

  public static int count(Connection connection)
      throws SQLException {
    int response = 0;
    String sql = "SELECT COUNT(*) AS _count FROM _contract_details";
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
    String sql = "INSERT INTO _contract_details (_id, _quantity, _unit_price, _work, _contract) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, getId());
    statement.setDouble(2, getQuantity());
    statement.setDouble(3, getUnitPrice());
    statement.setString(4, getWork().getId());
    statement.setString(5, getContract().getId());
    statement.execute();
    statement.close();
  }

  public void update(Connection connection)
      throws SQLException {
    String sql = "UPDATE _contract_details SET _quantity = ?, _unit_price = ?, _work = ?, _contract = ? WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setDouble(1, getQuantity());
    statement.setDouble(2, getUnitPrice());
    statement.setString(3, getWork().getId());
    statement.setString(4, getContract().getId());
    statement.setString(5, getId());
    statement.execute();
    statement.close();
  }

  public static void delete(Connection connection, String id)
      throws SQLException {
    String sql = "DELETE FROM _contract_details WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
    statement.close();
  }

  public static ContractDetails createFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    ContractDetails contractdetails = new ContractDetails();

    contractdetails.setId(resultSet.getString("_id"));
    contractdetails.setQuantity(resultSet.getDouble("_quantity"));
    contractdetails.setUnitPrice(resultSet.getDouble("_unit_price"));

    Work work = new Work();
    work.setId(resultSet.getString("_work_id"));
    work.setName(resultSet.getString("_work_name"));
    contractdetails.setWork(work);

    ClientContract contract = new ClientContract();
    contract.setId(resultSet.getString("_contract"));
    contractdetails.setContract(contract);

    Unit unit = new Unit();
    unit.setName(resultSet.getString("_unit_name"));
    contractdetails.setUnit(unit);

    return contractdetails;
  }

  public static ContractDetails findById(Connection connection, String id)
      throws SQLException {
    ContractDetails response = null;
    String sql = "SELECT * FROM _v_main_contract_details WHERE _id = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, id);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ContractDetails contractdetails = createFromResultSet(connection, resultSet);
      response = contractdetails;
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ContractDetails> findAll(Connection connection)
      throws SQLException {
    List<ContractDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_contract_details";
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ContractDetails contractdetails = createFromResultSet(connection, resultSet);
      response.add(contractdetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ContractDetails> findAllByContract(Connection connection, ClientContract contract)
      throws SQLException {
    List<ContractDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_contract_details WHERE _contract = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, contract.getId());
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ContractDetails contractdetails = createFromResultSet(connection, resultSet);
      response.add(contractdetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

  public static List<ContractDetails> findByPage(Connection connection, int page, int count)
      throws SQLException {
    List<ContractDetails> response = new ArrayList<>();
    String sql = "SELECT * FROM _v_main_contract_details LIMIT ? OFFSET ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setInt(1, count);
    statement.setInt(2, (page - 1) * count);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ContractDetails contractdetails = createFromResultSet(connection, resultSet);
      response.add(contractdetails);
    }
    statement.close();
    resultSet.close();
    return response;
  }

}
