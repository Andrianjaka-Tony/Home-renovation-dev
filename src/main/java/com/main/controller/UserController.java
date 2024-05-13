package com.main.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.helper.Connect;
import com.main.model.User;
import com.main.util.Response;

@RestController
public class UserController {

  @PostMapping(value = "/auth/sign-in", produces = "application/json")
  public Response signIn(@RequestBody User user) {
    Connection connection = Connect.getConnection();
    try {
      String token = user.signIn(connection);
      Response response = new Response(200, "Sign in success.");
      response.add("token", token);
      response.add("role", user.getRole());
      return response;
    } catch (Exception e) {
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/auth/sign-up", produces = "application/json")
  public Response signUp(@RequestBody User user) {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      user.signUp(connection);
      connection.commit();
      Response response = new Response(200, "Sign up success.");
      return response;
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

  @PostMapping(value = "/reset-database", produces = "application/json")
  public Response resetDatabase() {
    Connection connection = Connect.getConnection();
    try {
      Connect.startTransaction(connection);
      Connect.resetDatabase(connection);
      connection.commit();
      return new Response(200, "Databse reset");
    } catch (Exception e) {
      Connect.rollback(connection);
      Response response = new Response(403, e.getMessage());
      return response;
    } finally {
      Connect.close(connection);
    }
  }

}
