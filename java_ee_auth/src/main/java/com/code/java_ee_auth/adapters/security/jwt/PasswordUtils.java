package com.code.web_java_ee.adapters.security.jwt;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
