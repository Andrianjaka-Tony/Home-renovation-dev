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
public class HouseDetailsController {

  @GetMapping(value = "/api/house-details/save", produces = "application/json")
  public Response save() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "Data");
      response.add("houses", House.findAll(connection));
      response.add("works", Work.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/api/house-details/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody HouseDetails housedetails) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      housedetails.save(connection);
      connection.commit();
      Response response = new Response(200, "HOUSE-DETAILS saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/house-details/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody HouseDetails housedetails) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      housedetails.update(connection);
      connection.commit();
      Response response = new Response(200, "HOUSE-DETAILS updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/house-details/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      HouseDetails.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "HOUSE-DETAILS deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/house-details/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE-DETAILS found");
      response.add("house-details", HouseDetails.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/house-detailss", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE-DETAILS list");
      response.add("array", HouseDetails.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/house-detailss/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE-DETAILS list");
      response.add("count", HouseDetails.count(connection));
      response.add("array", HouseDetails.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}