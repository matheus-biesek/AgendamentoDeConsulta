package com.code.java_ee_schedule.adpters.in.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.code.java_ee_schedule.adpters.in.service.RoomService;
import com.code.java_ee_schedule.domain.dto.UpdateRoomDTO;
import com.code.java_ee_schedule.domain.model.Room;

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

@Path("/room")
@ApplicationScoped
public class RoomRest {

    @Inject
    private RoomService roomService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(UpdateRoomDTO dto) {
        try {
            UUID roomId = roomService.create(dto.getNumber(), dto.getFunction().toString(), dto.getUserId());
            return Response.status(Response.Status.CREATED).entity("Sala criada com sucesso: " + roomId).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao criar sala").build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoom(UpdateRoomDTO dto) {
        try {
            Room room = null;
            if (dto.getUserId() == null) {
                room = new Room(dto.getId(), dto.getNumber(), dto.getFunction().toString());
            } else {
                room = new Room(dto.getId(), dto.getNumber(), dto.getFunction().toString(), dto.getUserId());
            }
            roomService.updateRoom(room);
            return Response.status(Response.Status.OK).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao atualizar sala").build();
        }
    }
    
    @GET
    @Path("/search-all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchAll() {
        try {
            List<Room> rooms = roomService.getAllRooms();
            return Response.status(Response.Status.OK).entity(rooms).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao buscar sala").build();
        }
    }

    // OFFLINE
    @GET
    @Path("/search-by-user-id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByUserId(UpdateRoomDTO dto) {
        try {
            List<Room> rooms = roomService.getRoomsByUserId(dto.getUserId());
            Map<String, List<Room>> response = new HashMap<>();
            response.put("rooms", rooms);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao buscar sala").build();
        }
    }
}
