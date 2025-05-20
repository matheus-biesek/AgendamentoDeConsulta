package com.code.java_ee_schedule.adpters.in.rest;

import com.code.java_ee_schedule.adpters.in.service.AppointmentService;
import com.code.java_ee_schedule.domain.dto.UpdateAppointmentDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/appointment")
@ApplicationScoped
public class AppointmentRest {

    @Inject
    private AppointmentService appointmentService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(UpdateAppointmentDTO dto) {
        try {
            appointmentService.create(dto.getTimeSlotId(), dto.getPatientId());
            return Response.status(Response.Status.CREATED).entity("Consulta agendada com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao criar consulta").build();
        }
    }

    @PUT
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(UpdateAppointmentDTO dto) {
        try {
            appointmentService.cancel(dto.getAppointmentId());
            return Response.status(Response.Status.OK).entity("Consulta cancelada com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao cancelar consulta").build();
        }
    }

    @GET
    @Path("/find-all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            return Response.status(Response.Status.OK).entity(appointmentService.findAll()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao buscar consultas").build();
        }
    }
}
