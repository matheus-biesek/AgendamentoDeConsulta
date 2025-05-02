package com.code.java_ee_auth.adapters.out.rest.exeception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
