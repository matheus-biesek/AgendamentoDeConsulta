package com.code.java_ee_workers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/hello-world")
public class HelloResource {
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
            TesteProducer producer = new TesteProducer();
            String response = producer.call(role.role());
            producer.close();
            return response;
        } catch (IOException | TimeoutException | InterruptedException e) {
            return "Erro ao buscar usuários: " + e.getMessage();
        }
    }

    @GET
    @Path("/user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUserData(dto uuid) {
        try {
            SearchUserProducer producer = new SearchUserProducer();
            String response = producer.call(uuid.uuid());
            producer.close();
            return response;
        } catch (IOException | TimeoutException | InterruptedException e) {
            return "Erro ao buscar usuários: " + e.getMessage();
        }
    }
}