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
public class UnitController {

  @PostMapping(value = "/api/unit/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody Unit unit) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      unit.save(connection);
      connection.commit();
      Response response = new Response(200, "UNIT saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/unit/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody Unit unit) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      unit.update(connection);
      connection.commit();
      Response response = new Response(200, "UNIT updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/unit/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      Unit.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "UNIT deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/unit/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "UNIT found");
      response.add("unit", Unit.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/units", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "UNIT list");
      response.add("array", Unit.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/units/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "UNIT list");
      response.add("count", Unit.count(connection));
      response.add("array", Unit.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}