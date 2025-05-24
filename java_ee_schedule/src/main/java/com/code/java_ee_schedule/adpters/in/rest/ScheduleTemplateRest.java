package com.code.java_ee_schedule.adpters.in.rest;

import com.code.java_ee_schedule.adpters.in.service.ScheduleTemplateService;
import com.code.java_ee_schedule.domain.dto.MessageDTO;
import com.code.java_ee_schedule.domain.dto.UpdateScheduleTemplateDTO;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;

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
            MessageDTO message = new MessageDTO("Template de agendamento criado com sucesso");
            return Response.status(Response.Status.CREATED).entity(message).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro ao criar template de agendamento");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(UpdateScheduleTemplateDTO scheduleTemplateDTO) {
        try {
            scheduleTemplateService.update(scheduleTemplateDTO);
            MessageDTO message = new MessageDTO("Template de agendamento atualizado com sucesso");
            return Response.status(Response.Status.OK).entity(message).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro ao atualizar template de agendamento");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/find-by-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(UpdateScheduleTemplateDTO scheduleTemplateDTO) {
        try {
            ScheduleTemplate scheduleTemplate = scheduleTemplateService.findById(scheduleTemplateDTO.getScheduleTemplateId());
            UpdateScheduleTemplateDTO dto = new UpdateScheduleTemplateDTO(scheduleTemplate.getSchedule_template_id(), scheduleTemplate.getRoom_id(), scheduleTemplate.getUser_id(), scheduleTemplate.getDay_of_week(), scheduleTemplate.getStart_time(), scheduleTemplate.getEnd_time(), scheduleTemplate.getSlot_duration_minutes());
            return Response.status(Response.Status.OK).entity(dto).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro ao buscar template de agendamento");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }


    @POST
    @Path("/find-by-user-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByUserId(UpdateScheduleTemplateDTO scheduleTemplateDTO) {
        try {
            return Response.status(Response.Status.OK).entity(scheduleTemplateService.findAllByUserId(scheduleTemplateDTO.getUserId())).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro ao buscar template de agendamento");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }


    // OFFLINE
    @GET
    @Path("/find-all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            return Response.status(Response.Status.OK).entity(scheduleTemplateService.findAll()).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            MessageDTO message = new MessageDTO("Erro ao buscar templates de agendamento");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}