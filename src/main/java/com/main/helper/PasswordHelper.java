package com.main.helper;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHelper {

  public static String encodePassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public static boolean checkPassword(String password, String hashedPassword) {
    return BCrypt.checkpw(password, hashedPassword);
  }

}
