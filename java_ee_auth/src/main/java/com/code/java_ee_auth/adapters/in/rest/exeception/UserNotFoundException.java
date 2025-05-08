package com.code.java_ee_auth.adapters.in.rest.exeception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
