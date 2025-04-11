package com.code.java_ee_auth.adapters.rest;

import com.code.java_ee_auth.application.service.UserServiceImpl;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.request.UsernameDTO;
import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserRest {

    @Inject
    private UserServiceImpl userService;

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid UserInfoDTO userInfoDTO) {
        MessageDTO message = userService.createUser(userInfoDTO);
        return Response.status(Response.Status.CREATED).entity(message).build();
    }

    @POST
    @Path("/search-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDataDTO searchData(@Valid UsernameDTO usernameDTO) {
        return userService.getUserData(usernameDTO.getUsername());
    }

    @PUT
    @Path("/update-role")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public MessageDTO updateRole(@Valid UpdateRoleDTO updateRoleDTO) {
        return userService.updateUserRole(updateRoleDTO);
    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public MessageDTO delete(@Valid UsernameDTO usernameDTO) {
        return userService.deleteUser(usernameDTO);
    }
}
