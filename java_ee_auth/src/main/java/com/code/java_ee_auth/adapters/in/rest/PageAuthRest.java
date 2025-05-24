package com.code.java_ee_auth.adapters.in.rest;

import java.util.logging.Logger;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth-front")    
public class PageAuthRest {

    private static final Logger logger = Logger.getLogger(PageAuthRest.class.getName());

    @GET
    @Path("/validate-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@CookieParam("refreshToken") String refreshToken) {
        logger.info("refreshToken: " + refreshToken);
        return Response.ok().build();
    }

    @GET
    @Path("/validate-secretary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateSecretary() {
        return Response.ok().build();
    }

    @GET
    @Path("/validate-professional")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateProfessional() {
        return Response.ok().build();
    }

    @GET
    @Path("/validate-patient")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validatePatient() {
        return Response.ok().build();
    }
}
