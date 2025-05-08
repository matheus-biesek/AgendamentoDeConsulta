package com.code.java_ee_workers.adapters.in.rest;

import com.code.java_ee_workers.adapters.out.producer.SearchUserByRoleProducer;
import com.code.java_ee_workers.adapters.out.producer.SearchUserProducer;
import com.code.java_ee_workers.domain.dto.dto;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/workers")
public class WorkersRest {

    @Inject
    private SearchUserProducer searchUserProducer;
    @Inject
    private SearchUserByRoleProducer searchUserByRoleProducer;

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    public String publicEndpoint() {
        return "Endpoint público!";
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
