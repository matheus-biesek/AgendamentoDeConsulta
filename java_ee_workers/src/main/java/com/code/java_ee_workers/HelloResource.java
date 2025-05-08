package com.code.java_ee_workers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/hello-world")
public class HelloResource {

    @Inject
    private SearchUserProducer searchUserProducer;
    @Inject
    private SearchUserByRoleProducer searchUserByRoleProducer;

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }

    @GET
    @Path("/users-by-role")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUsersByRole(dto role) {
        try {
            String response = searchUserByRoleProducer.sendAndReceive(role.role());
            System.out.println("Testando nova alteração!");
            return response;
        } catch (Exception e) {
            return "Erro ao buscar usuários: " + e.getMessage();
        }
    }

    @GET
    @Path("/user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUserData(dto uuid) {
        try {
            String response = searchUserProducer.sendAndReceive(uuid.uuid());
            return response;
        } catch (Exception e) {
            return "Erro ao buscar usuários: " + e.getMessage();
        }
    }
}