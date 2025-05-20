package com.code.java_ee_schedule.adpters.in.rest;

import java.util.UUID;

import com.code.java_ee_schedule.adpters.in.service.ScheduleTemplateService;
import com.code.java_ee_schedule.domain.dto.UpdateScheduleTemplateDTO;
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

@Path("/schedule-template")
@ApplicationScoped
public class ScheduleTemplateRest {

    @Inject
    private ScheduleTemplateService scheduleTemplateService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)   
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(UpdateScheduleTemplateDTO scheduleTemplateDTO) {
        try {
            scheduleTemplateService.create(scheduleTemplateDTO);
            return Response.status(Response.Status.CREATED).entity("Template de agendamento criado com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar template de agendamento").build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(UpdateScheduleTemplateDTO scheduleTemplateDTO) {
        try {
            scheduleTemplateService.update(scheduleTemplateDTO);
            return Response.status(Response.Status.OK).entity("Template de agendamento atualizado com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar template de agendamento").build();
        }
    }

    @GET
    @Path("/find-all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            return Response.status(Response.Status.OK).entity(scheduleTemplateService.findAll()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar templates de agendamento").build();
        }
    }
}