package com.code.java_ee_schedule.adpters.in.rest;

import com.code.java_ee_schedule.adpters.in.security.SecurityUtils;
import com.code.java_ee_schedule.adpters.in.service.AppointmentService;
import com.code.java_ee_schedule.adpters.in.service.TimeSlotService;
import com.code.java_ee_schedule.domain.dto.MessageDTO;
import com.code.java_ee_schedule.domain.dto.UpdateAppointmentDTO;
import com.code.java_ee_schedule.domain.dto.UserIdDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@Path("/appointment")
@ApplicationScoped
public class AppointmentRest {

    @Inject
    private AppointmentService appointmentService;
    @Inject
    private TimeSlotService timeSlotService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(UpdateAppointmentDTO dto) {
        try {
            appointmentService.create(dto.getTimeSlotId(), dto.getPatientId());
            MessageDTO message = new MessageDTO("Consulta agendada com sucesso");
            return Response.status(Response.Status.CREATED).entity(message).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDTO message = new MessageDTO("Erro inesperado ao criar consulta");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(UpdateAppointmentDTO dto) {
        try {
            UpdateAppointmentDTO appointment = appointmentService.findById(dto.getAppointmentId());
            appointmentService.cancel(dto.getAppointmentId());
            timeSlotService.cancel(appointment.getTimeSlotId(), true);
            MessageDTO message = new MessageDTO("Consulta cancelada com sucesso, grade horária reativada");
            return Response.status(Response.Status.OK).entity(message).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDTO message = new MessageDTO("Erro inesperado ao cancelar consulta");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
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

    @POST
    @Path("/find-by-user-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByUserId(@Valid UserIdDTO dto) {
        try {
            List<UpdateAppointmentDTO> appointments = appointmentService.findByUserId(dto.getUserId());
            return Response.status(Response.Status.OK).entity(appointments).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado ao buscar consultas").build();
        }
    }

    @GET
    @Path("/my-appointments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMyAppointments(@Context SecurityContext securityContext) {
        try {
            UUID userId = SecurityUtils.getUserId(securityContext);
            return Response.status(Response.Status.OK)
                .entity(appointmentService.findByUserId(userId))
                .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro inesperado ao buscar suas consultas")
                .build();
        }
    }

    @GET
    @Path("/find-by-professional-id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAppointmentsByProfessionalId(@Context SecurityContext securityContext) {
        try {
            UUID userId = SecurityUtils.getUserId(securityContext);
            List<UpdateAppointmentDTO> appointments = appointmentService.findAppointmentsByProfessionalId(userId);
            return Response.status(Response.Status.OK).entity(appointments).build();
        } catch (RuntimeException e) {
            MessageDTO message = new MessageDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDTO message = new MessageDTO("Erro inesperado ao buscar consultas");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
