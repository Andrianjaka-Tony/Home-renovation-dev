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
public class FinishingTypeController {

  @PostMapping(value = "/api/finishing-type/save", consumes = "application/json", produces = "application/json")
  public Response save(@RequestBody FinishingType finishingtype) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      finishingtype.save(connection);
      connection.commit();
      Response response = new Response(200, "FINISHING-TYPE saved");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PutMapping(value = "/api/finishing-type/update", consumes = "application/json", produces = "application/json")
  public Response update(@RequestBody FinishingType finishingtype) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      finishingtype.update(connection);
      connection.commit();
      Response response = new Response(200, "FINISHING-TYPE updated");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @DeleteMapping(value = "/api/finishing-type/delete/{id}", produces = "application/json")
  public Response delete(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      FinishingType.delete(connection, id);
      connection.commit();
      Response response = new Response(200, "FINISHING-TYPE deleted");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/finishing-type/{id}", produces = "application/json")
  public Response findById(@PathVariable(name = "id") String id) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "FINISHING-TYPE found");
      response.add("finishing-type", FinishingType.findById(connection, id));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/finishing-types", produces = "application/json")
  public Response findAll() {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "FINISHING-TYPE list");
      response.add("array", FinishingType.findAll(connection));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @GetMapping(value = "/api/finishing-types/{page}/{count}", produces = "application/json")
  public Response findByPage(@PathVariable(name = "page") int page, @PathVariable(name = "count") int count) {
    Connection connection = Connect.getConnection();
    try {
      Response response = new Response(200, "FINISHING-TYPE list");
      response.add("count", FinishingType.count(connection));
      response.add("array", FinishingType.findByPage(connection, page, count));
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}