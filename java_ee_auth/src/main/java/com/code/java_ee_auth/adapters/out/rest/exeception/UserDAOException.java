package com.code.java_ee_auth.adapters.out.rest.exeception;

import jakarta.ws.rs.core.Response;

public class UserDAOException extends RuntimeException {
    private final Response.Status httpStatus;

    public UserDAOException(String message, Response.Status httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public UserDAOException(String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public Response.Status getHttpStatus() {
        return httpStatus;
    }
}
