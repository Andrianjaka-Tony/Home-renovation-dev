package com.main.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.helper.Connect;
import com.main.util.Response;

import com.main.model.*;

@RestController
@CrossOrigin(origins = "*")
public class ClientPaymentController {

  @GetMapping(value = "/api/client-payment/save", produces = "application/json")
  public Response save() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data");
      response.add("client-contracts", ClientContract.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/client-payment/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody ClientPayment clientpayment) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      clientpayment.save(connection);
      connection.commit();
      Response response = new Response(200, "CLIENT-PAYMENT saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/client-payment/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody ClientPayment clientpayment) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      clientpayment.update(connection);
      connection.commit();
      Response response = new Response(200, "CLIENT-PAYMENT updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/client-payment/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      ClientPayment.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "CLIENT-PAYMENT deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-payment/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-PAYMENT found");
      response.add("client-payment", ClientPayment.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-payments", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-PAYMENT list");
      response.add("array", ClientPayment.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-payments/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-PAYMENT list");
      response.add("count", ClientPayment.count(connection));
      response.add("array", ClientPayment.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}