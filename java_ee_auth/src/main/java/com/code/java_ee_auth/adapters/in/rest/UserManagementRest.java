package com.code.java_ee_auth.adapters.in.rest;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.code.java_ee_auth.adapters.in.services.user.UserCrudServiceImpl;
import com.code.java_ee_auth.adapters.in.services.user.UserRoleService;
import com.code.java_ee_auth.domain.dto.request.UserUpdateDTO;
import com.code.java_ee_auth.domain.dto.request.CpfDTO;
import com.code.java_ee_auth.domain.dto.request.GetUsersByRoleAndActiveDTO;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user-management")
public class UserManagementRest {

    private static final Logger logger = Logger.getLogger(UserManagementRest.class.getName());
    @Inject
    private UserCrudServiceImpl userCrudService;

    @Inject
    private UserRoleService userRoleService;

    // COLETAR O ID DO USER QUE ESTÁ NO SECURITY CONTEXT
    // ADICIONAR DTO PARA CADA ENDPOINT E CONVERTER PARA O DTO PARA UPDATE DE USER.
    
    @POST
    @Path("/register-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserInfoDTO newUser) {
        try {
            if (newUser.getRole() != null) {
                userCrudService.create(newUser, "patient");    
            } else {
                userCrudService.create(newUser, null);
            }
            MessageDTO message = new MessageDTO("Usuário registrado com sucesso");
            return Response.status(Response.Status.CREATED).entity(message).build();
        
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao registrar usuário!")
            .build();
        }
    }

    @POST
    @Path("/search-user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchData(@Valid CpfDTO cpfDTO) {
        try {
            UserDataDTO userData = userCrudService.getUserData(cpfDTO.getCpf());
            return Response.status(Response.Status.OK)
            .entity(userData)
            .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao buscar dados do usuário!")
            .build();
        }
    }

    @POST
    @Path("/delete-or-activate-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Valid UserUpdateDTO dto) {
        if (dto.getActive() == null) {
            return Response.status(Response.Status.CONFLICT)
            .entity("O campo active não pode ser nulo!")
            .build();
        }
        try {
            userCrudService.updateByCpf(dto);
            MessageDTO message = new MessageDTO("Usuário " + (dto.getActive() ? "ativado" : "deletado") + " com sucesso");
            return Response.status(Response.Status.OK)
            .entity(message)
            .build();
        
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();
        
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao " + (dto.getActive() ? "ativrar" : "deletar") + " usuário!")
            .build();
        }
    }

    @POST
    @Path("/update-user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserSecretary(@Valid UserUpdateDTO dto) {
        try {
            userCrudService.updateByCpf(dto);
            MessageDTO message = new MessageDTO("Usuário atualizado com sucesso");
            return Response.status(Response.Status.OK)
            .entity(message)
            .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao atualizar usuário!")
            .build();
        }
    }

    @POST
    @Path("/add-role-to-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoleToUser(@Valid UpdateRoleDTO dto) {
        if (dto.getRole() == null) {
            return Response.status(Response.Status.CONFLICT)
            .entity("O campo role não pode ser nulo!")
            .build();
        }
        try {
            userRoleService.addRoleToUser(UUID.fromString(dto.getId()), dto.getRole().toString().toLowerCase());
            MessageDTO message = new MessageDTO("Função adicionada ao usuário com sucesso");
            return Response.status(Response.Status.OK)
            .entity(message)
            .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao adicionar função ao usuário!")
            .build();
        }
    }

    @DELETE
    @Path("/remove-role-from-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeRoleFromUser(@Valid UpdateRoleDTO dto) {
        if (dto.getRole() == null) {
            return Response.status(Response.Status.CONFLICT)
            .entity("O campo role não pode ser nulo!")
            .build();
        }
        try {
            userRoleService.removeRoleFromUser(UUID.fromString(dto.getId()), dto.getRole().toString().toLowerCase());
            MessageDTO message = new MessageDTO("Função removida do usuário com sucesso");
            return Response.status(Response.Status.OK)
            .entity(message)
            .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao remover função do usuário!")
            .build();
        }
    }

    @POST
    @Path("/block-or-unblock-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response blockUser(@Valid UserUpdateDTO dto) {
        if (dto.getBlocked() == null) {
            return Response.status(Response.Status.CONFLICT)
            .entity("O campo blocked não pode ser nulo!")
            .build();
        }
        try {
            userCrudService.updateByCpf(dto);
            MessageDTO message = new MessageDTO("Usuário " + (dto.getBlocked() ? "bloqueado" : "desbloqueado") + " com sucesso");
            return Response.status(Response.Status.OK)
            .entity(message)
            .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao " + (dto.getBlocked() ? "bloquear" : "desbloquear") + " usuário!")
            .build();
        }
    }

    @POST
    @Path("/get-users-by-role-and-active")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsersByRoleAndActive(@Valid GetUsersByRoleAndActiveDTO dto) {
        try {
            List<UserDataDTO> users = userCrudService.getUsersByRoleAndActive(dto.getRole(), dto.isActive());
            return Response.status(Response.Status.OK).entity(users).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao buscar usuários por função e status!")
            .build();
        }
    }
}
