package com.main.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.helper.Connect;
import com.main.model.ClientContract;
import com.main.model.ClientPayment;
import com.main.model.ImportData;
import com.main.util.Response;

@RestController
@CrossOrigin(origins = "*")
public class AdminController {

  @GetMapping(value = "/api/admin/dashboard", produces = "application/json")
  public Response findAllByContracts() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data fetched");
      response.add("price", ClientContract.totalPrice(connection));
      response.add("payment", ClientPayment.totalPayment(connection));
      response.add("histogram", ClientContract.histogram(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/admin/import/house-contract", consumes = "application/json")
  public Response uploadHouseAndContract(@RequestBody ImportData data) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data imported");
      Connect.startTransaction(connection);
      data.process(connection);
      connection.commit();
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/admin/import/payment", consumes = "application/json")
  public Response uploadPayments(@RequestBody ImportData data) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data imported");
      Connect.startTransaction(connection);
      data.importPayment(connection);
      connection.commit();
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}
