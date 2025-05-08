package com.code.java_ee_auth.adapters.in.rest.exeception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
