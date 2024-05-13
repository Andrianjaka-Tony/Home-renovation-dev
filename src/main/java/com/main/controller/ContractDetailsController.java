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
public class ContractDetailsController {

  @GetMapping(value = "/api/contract-details/save", produces = "application/json")
  public Response save() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data");
      response.add("works", Work.findAll(connection));
      response.add("client-contracts", ClientContract.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/contract-details/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody ContractDetails contractdetails) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      contractdetails.save(connection);
      connection.commit();
      Response response = new Response(200, "CONTRACT-DETAILS saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/contract-details/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody ContractDetails contractdetails) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      contractdetails.update(connection);
      connection.commit();
      Response response = new Response(200, "CONTRACT-DETAILS updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/contract-details/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      ContractDetails.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "CONTRACT-DETAILS deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/contract-details/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CONTRACT-DETAILS found");
      response.add("contract-details", ContractDetails.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/contract-detailss", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CONTRACT-DETAILS list");
      response.add("array", ContractDetails.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/contract-detailss/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "CONTRACT-DETAILS list");
      response.add("count", ContractDetails.count(connection));
      response.add("array", ContractDetails.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}