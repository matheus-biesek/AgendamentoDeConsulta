package com.code.java_ee_auth.adapters.rest.exeception;

public class UserNoExistException extends RuntimeException {
    public UserNoExistException(String message) {
        super(message);
    }
}
