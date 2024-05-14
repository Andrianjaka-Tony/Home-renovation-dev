package com.main.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.main.exception.PaymentException;
import com.main.helper.Connect;
import com.main.util.Response;

import com.main.model.*;

@RestController
@CrossOrigin(origins = "*")
public class ClientController {

  @PostMapping(value = "/api/client/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody Client client) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      client.save(connection);
      connection.commit();
      Response response = new Response(200, "CLIENT saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/client/sign-in", consumes = "application/json", produces = "application/json")
  public Response signIn(@RequestBody Client client) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      client.signIn(connection);
      connection.commit();
      Response response = new Response(200, "Sign-in Success");
      response.add("contact", client.getContact());
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/client/payment", consumes = "application/json", produces = "application/json")
  public Response payment(@RequestBody ClientPayment payment,
      @RequestHeader(name = "Authorization") String authorization) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      Client client = Client.findByContact(connection, authorization.substring(7));
      client.payment(connection, payment);
      connection.commit();
      Response response = new Response(200, "Sign-in Success");
      return response;
    } catch (PaymentException e) {
      Connect.rollback(connection);
      Response response = new Response(201, e.getMessage());
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/client/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody Client client) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      client.update(connection);
      connection.commit();
      Response response = new Response(200, "CLIENT updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/client/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      Client.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "CLIENT deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT found");
      response.add("client", Client.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/clients", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT list");
      response.add("array", Client.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/clients/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT list");
      response.add("count", Client.count(connection));
      response.add("array", Client.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-contracts/{contact}", produces = "application/json")
  public Response findAllByContracts(@PathVariable(name = "contact") String contact) {
    Connection connection = Connect.getConnection();
    try {
      Client client = new Client();
      client.setContact(contact);
      Response response = new Response(200, "CLIENT-CONTRACT list");
      response.add("array", ClientContract.findAllByClient(connection, client));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}