package com.code.java_ee_auth.adapters.out.rest.exeception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
