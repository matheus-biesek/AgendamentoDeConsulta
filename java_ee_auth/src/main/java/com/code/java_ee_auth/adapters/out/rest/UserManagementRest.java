package com.code.java_ee_auth.adapters.out.rest;

import com.code.java_ee_auth.adapters.in.services.user.UserCrudServiceImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.out.rest.exeception.UserAlreadyExistsException;
import com.code.java_ee_auth.adapters.out.rest.exeception.UserNotFoundException;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.request.ChangeDataUserDTO;
import com.code.java_ee_auth.domain.dto.request.CpfDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.adapters.in.services.security.PasswordService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/user-management")
public class UserManagementRest {

    @Inject
    private UserDAOImpl userRepository;
    
    @Inject
    private UserCrudServiceImpl userCrudService;
    
    @Inject
    private PasswordService passwordService;

    @POST
    @Path("/register-secretary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSecretary(UserInfoDTO newUser) {
        Optional<User> userExist = userRepository.findByCpf(newUser.getCpf());
        if (userExist.isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String hashedPassword = passwordService.hash(newUser.getPassword());

        User user = new User(newUser.getName(), newUser.getCpf(), newUser.getEmail(), hashedPassword, UserRole.PATIENT, newUser.getGender());
        userRepository.create(user);

        return Response.status(Response.Status.CREATED).entity("Usuário registrado com sucesso").build();
    }

    @POST
    @Path("/register-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAdmin(UserInfoDTO newUser) {
        Optional<User> userExist = userRepository.findByCpf(newUser.getCpf());
        if (userExist.isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String hashedPassword = passwordService.hash(newUser.getPassword());

        User user = new User(newUser.getName(), newUser.getCpf(), newUser.getEmail(), hashedPassword, newUser.getRole(), newUser.getGender());
        userRepository.create(user);

        return Response.status(Response.Status.CREATED).entity("Usuário registrado com sucesso").build();
    }

    @POST
    @Path("/search-user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDataDTO searchData(@Valid CpfDTO cpfDTO) {
        return userCrudService.getUserData(cpfDTO.getCpf());
    }

    @POST
    @Path("/delete-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (!userExist.get().isActive()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário não está ativo").build();
            }
        }
        return Response.status(Response.Status.OK).entity(userCrudService.deleteUser(cpfDTO.getCpf())).build();
    }

    @POST
    @Path("/activate-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response activateUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (userExist.get().isActive()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário já está ativo").build();
            }
            userExist.get().setActive(true);
            userRepository.updateActive(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário ativado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }

    @POST
    @Path("/update-user-secretary")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserSecretary(@Valid ChangeDataUserDTO changeDataUserDTO) {
        return Response.status(Response.Status.OK).entity(userCrudService.updateUserSecretary(changeDataUserDTO)).build();
    }

    @POST
    @Path("/update-user-admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserAdmin(@Valid UpdateRoleDTO updateRoleDTO) {
        return Response.status(Response.Status.OK).entity(userCrudService.updateUserAdmin(updateRoleDTO)).build();
    }

    @POST
    @Path("/block-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response blockUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (userExist.get().isBlocked()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário já está bloqueado").build();
            }
            userExist.get().setBlocked(true);
            userExist.get().setActive(false);
            userRepository.updateBlocked(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário bloqueado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }

    @POST
    @Path("/unblock-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unblockUser(@Valid CpfDTO cpfDTO) { 
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (!userExist.get().isBlocked()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário não está bloqueado").build();
            }
            userExist.get().setBlocked(false);
            userRepository.updateBlocked(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário desbloqueado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }
} 