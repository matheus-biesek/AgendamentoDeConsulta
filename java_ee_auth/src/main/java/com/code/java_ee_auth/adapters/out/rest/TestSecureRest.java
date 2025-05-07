package com.code.java_ee_auth.adapters.out.rest;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/secure")
public class TestSecureRest {

    @POST
    @Path("/admin")
    public String adminOnly() {
        return "Apenas ADMIN pode ler está mensagem";
    }

    @POST
    @Path("/patient")
    public String userAccess() {
        return "Apenas PATIENT pode ler está mensagem";
    }
}

