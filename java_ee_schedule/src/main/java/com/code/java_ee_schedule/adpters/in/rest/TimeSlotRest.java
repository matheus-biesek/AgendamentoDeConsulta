package com.code.java_ee_schedule.adpters.in.rest;

import java.util.UUID;

import com.code.java_ee_schedule.adpters.in.security.SecurityUtils;
import com.code.java_ee_schedule.adpters.in.service.TimeSlotService;
import com.code.java_ee_schedule.domain.dto.CreateTimeSlotsDTO;
import com.code.java_ee_schedule.domain.dto.MessageDTO;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
            MessageDTO message = new MessageDTO("Grade de horários criada com sucesso");
            return Response.status(Response.Status.CREATED).entity(message).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro inesperado ao criar grade de horários");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/find-all-by-user-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByUserId(UpdateScheduleTemplateDTO updateScheduleTemplateDTO) {
        try {
            return Response.status(Response.Status.OK).entity(timeSlotService.findAllByUserId(updateScheduleTemplateDTO.getUserId(), true)).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro inesperado ao buscar grade de horários");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
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
            MessageDTO message = new MessageDTO("Grade de horários cancelada com sucesso");
            return Response.status(Response.Status.OK).entity(message).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro inesperado ao cancelar grade de horários");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }


    @GET
    @Path("/my-time-slots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMyTimeSlots(@Context SecurityContext securityContext) {
        try {
            UUID userId = SecurityUtils.getUserId(securityContext);
            return Response.status(Response.Status.OK).entity(timeSlotService.findAllByUserId(userId, true)).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro inesperado ao buscar grade de horários");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
