package com.main.util;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Response {

  private int status;
  private String message;
  private Map<String, Object> data;

  public Response(int status, String message) {
    setStatus(status);
    setMessage(message);
    setData(new HashMap<>());
  }

  public void add(String key, Object value) {
    getData().put(key, value);
  }

}
