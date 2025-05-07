package com.code.java_ee_schedule;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/admin")
public class AdminRest {

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String publicc() {
        return "Endpoint publico";
    }

    @GET
    @Path("/private")
    @Produces("text/plain")
    public String privatee() {
        return "Endpoint de admin";
    }
}