package com.main.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.helper.Connect;
import com.main.model.ClientContract;
import com.main.util.Response;

@RestController
public class AdminController {

  @GetMapping(value = "/api/admin/dashboard", produces = "application/json")
  @CrossOrigin(origins = "*")
  public Response findAllByContracts() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data fetched");
      response.add("price", ClientContract.totalPrice(connection));
      response.add("histogram", ClientContract.histogram(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}
