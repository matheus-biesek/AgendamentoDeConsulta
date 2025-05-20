package com.code.java_ee_schedule.adpters.in.rest;

import com.code.java_ee_schedule.adpters.in.service.TimeSlotService;
import com.code.java_ee_schedule.domain.dto.CreateTimeSlotsDTO;
import com.code.java_ee_schedule.domain.dto.UpdateScheduleTemplateDTO;
import com.code.java_ee_schedule.domain.dto.UpdateTimeSlotDTO;
import com.code.java_ee_schedule.domain.model.TimeSlot;

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

@Path("/time-slot")
@ApplicationScoped
public class TimeSlotRest {

    @Inject
    private TimeSlotService timeSlotService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateTimeSlotsDTO createTimeSlotsDTO) {
        try {
            timeSlotService.createSlots(createTimeSlotsDTO);
            return Response.status(Response.Status.CREATED).entity("Grade de horários criada com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao criar grade de horários").build();
        }
    }

    @GET
    @Path("/find-all-by-user-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByUserId(UpdateScheduleTemplateDTO updateScheduleTemplateDTO) {
        try {
            return Response.status(Response.Status.OK).entity(timeSlotService.findAllByUserId(updateScheduleTemplateDTO.getUserId(), true)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao buscar grade de horários").build();
        }
    }

    @PUT
    @Path("/cancel-by-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(UpdateTimeSlotDTO updateTimeSlotDTO) {
        try {
            TimeSlot timeSlot = new TimeSlot(updateTimeSlotDTO.getTimeSlotId(),false);
            timeSlotService.update(timeSlot);
            // Precisa verificar se existem alguma consulta agendada para setar o active dela como false
            return Response.status(Response.Status.OK).entity("Grade de horários cancelada com sucesso").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao cancelar grade de horários").build();
        }
    }
}
