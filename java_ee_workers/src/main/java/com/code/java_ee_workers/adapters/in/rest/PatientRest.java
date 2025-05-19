package com.code.java_ee_workers.adapters.in.rest;

import com.code.java_ee_workers.domain.dto.request.UpdatePatientDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

import com.code.java_ee_workers.adapters.in.service.PatientService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@Path("/patient")
@ApplicationScoped
public class PatientRest {

    @Inject
    private PatientService patientService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPatient(UpdatePatientDTO dto) {
        try {
            if (dto.getUserId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("É necessário informar o ID do usuário").build();
            }
            UUID patientId = patientService.createPatient(dto.getUserId().toString(), dto.getAllergies(), dto.getBloodType(), dto.getHeight());
            return Response.status(Response.Status.CREATED).entity(patientId.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro intenro ao criar paciente").build();
        }
    }

    @GET
    @Path("/search-by-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPatient(UpdatePatientDTO dto) {
        if (dto.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("É necessário informar o ID do paciente").build();
        }
        try {
            return Response.status(Response.Status.OK).entity(patientService.search(dto.getId().toString())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao buscar paciente").build();
        }
    }

    @GET
    @Path("/search-by-user-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByUserId(UpdatePatientDTO dto) {
        if (dto.getUserId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("É necessário informar o ID do usuário").build();
        }
        try {
            return Response.status(Response.Status.OK).entity(patientService.searchByUserId(dto.getUserId().toString())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao buscar paciente").build();
        }
    }

    @GET
    @Path("/search-by-active")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByActive(UpdatePatientDTO dto) {
        if (dto.getActive() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("É necessário informar se o paciente está ativo").build();
        }
        try {
            return Response.status(Response.Status.OK).entity(patientService.searchByActive(dto.getActive())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao buscar pacientes ativos").build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePatient(UpdatePatientDTO dto) {
        try {
            patientService.updatePatient(dto);
            return Response.status(Response.Status.OK).entity("Paciente atualizado com sucesso").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao atualizar paciente").build();
        }
    }
}