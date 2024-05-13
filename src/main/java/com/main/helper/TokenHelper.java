package com.main.helper;

import java.security.Key;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.main.exception.TokenException;
import com.main.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class TokenHelper {

  private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

  public static String generateTokenFromEmail(String email) {
    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    return Jwts.builder()
        .setSubject(email)
        .setExpiration(expirationDate)
        .signWith(secretKey)
        .compact();
  }

  public static String extractEmailFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  public static String extractToken(String authorization) {
    return authorization.substring(7);
  }

  public static boolean isValidToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
      Date expirationDate = claims.getExpiration();
      return !expirationDate.before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public static User protect(Connection connection, String authorization)
      throws TokenException, SQLException {
    if (authorization == null) {
      throw new TokenException("The authorization is required.");
    }
    if (!authorization.startsWith("Bearer ")) {
      throw new TokenException("Wrong authorization.");
    }
    String token = extractToken(authorization);
    String email = extractEmailFromToken(token);
    User user = User.findByEmail(connection, email);
    if (user == null) {
      throw new TokenException("The user is does not exist.");
    }
    if (!isValidToken(token)) {
      throw new TokenException("THe token is expired, please sign in.");
    }
    return user;
  }

}
