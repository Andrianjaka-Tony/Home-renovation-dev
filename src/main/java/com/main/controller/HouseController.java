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
public class HouseController {

  @PostMapping(value = "/api/house/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody House house) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      house.save(connection);
      connection.commit();
      Response response = new Response(200, "HOUSE saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/house/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody House house) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      house.update(connection);
      connection.commit();
      Response response = new Response(200, "HOUSE updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/house/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      House.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "HOUSE deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/house/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE found");
      response.add("house", House.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/houses", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE list");
      response.add("array", House.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/houses/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "HOUSE list");
      response.add("count", House.count(connection));
      response.add("array", House.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}