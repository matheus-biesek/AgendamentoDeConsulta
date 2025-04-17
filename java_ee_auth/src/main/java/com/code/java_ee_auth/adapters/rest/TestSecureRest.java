package com.code.java_ee_auth.adapters.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/secure")
public class TestSecureRest {

    @POST
    @Path("/admin")
    public String adminOnly() {
        return "Apenas ADMIN pode acessar";
    }

    @POST
    @Path("/patient")
    public String userAccess() {
        return "Apenas PATIENT pode acessar";
    }
}

