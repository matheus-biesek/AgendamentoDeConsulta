package com.code.java_ee_auth.adapters.rest;

import com.code.java_ee_auth.application.service.security.AccessJWTService;
import com.code.java_ee_auth.domain.enuns.UserRole;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

@Path("/auth-front")
public class AuthFrontRest {

    @Inject
    private AccessJWTService accessJWTService;

    @GET
    @Path("/validate-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.ADMIN)) {
            System.out.println("Token não possui permissão de ADMIN.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/validate-secretary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateSecretary(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.SECRETARY)) {
            System.out.println("Token não possui permissão de SECRETARY.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/validate-nurse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateNurse(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.NURSE)) {
            System.out.println("Token não possui permissão de NURSE.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/validate-doctor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateDoctor(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.DOCTOR)) {
            System.out.println("Token não possui permissão de DOCTOR.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/validate-technician")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateTechnician(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.TECHNICIAN)) {
            System.out.println("Token não possui permissão de TECHNICIAN.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/validate-patient")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validatePatient(@CookieParam("token") String token) {
        if (token == null || token.isBlank()) {
            System.out.println("Token inválido ou ausente.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        System.out.println("Token recebido via Cookie: " + token);

        if (!accessJWTService.validateTokenRole(token, UserRole.PATIENT)) {
            System.out.println("Token não possui permissão de PATIENT.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }
}
