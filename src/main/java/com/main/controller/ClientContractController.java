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
public class ClientContractController {

  @GetMapping(value = "/api/client-contract/save", produces = "application/json")
  public Response save() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data");
      response.add("houses", House.findAll(connection));
      response.add("finishings", FinishingType.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/client-contract/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody ClientContract clientcontract) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      clientcontract.getClient().beginContract(connection, clientcontract);
      connection.commit();
      Response response = new Response(200, "CLIENT-CONTRACT saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/client-contract/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody ClientContract clientcontract) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      clientcontract.update(connection);
      connection.commit();
      Response response = new Response(200, "CLIENT-CONTRACT updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/client-contract/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      ClientContract.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "CLIENT-CONTRACT deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-contract/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-CONTRACT found");
      response.add("contract", ClientContract.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-contracts", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-CONTRACT list");
      response.add("array", ClientContract.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-contracts/current", produces = "application/json")
  public Response findAllCurrent() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-CONTRACT list");
      response.add("array", ClientContract.findAllCurrent(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/client-contracts/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CLIENT-CONTRACT list");
      response.add("count", ClientContract.count(connection));
      response.add("array", ClientContract.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}