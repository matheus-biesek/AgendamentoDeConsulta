package com.code.java_ee_auth.application.service;

import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.rest.exeception.UserDAOException;
import com.code.java_ee_auth.adapters.rest.exeception.UserNotFoundException;
import com.code.java_ee_auth.domain.dto.request.ChangeDataUserDTO;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.in.UserServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserServicePort {

    @Inject
    private UserDAOImpl userDAO;

    public UserDataDTO getUserData(String cpf) {
        Optional<User> user = userDAO.findByCpf(cpf);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Usuário não encontrado!");
        }
        return new UserDataDTO(user.get().getId(), user.get().isBlocked(), user.get().isActive(), user.get().getName(), user.get().getCpf(), user.get().getEmail(), user.get().getRole(), user.get().getGender());
    }

    public MessageDTO updateUserSecretary(ChangeDataUserDTO changeDataUserDTO) {
        Optional<User> userOpt = userDAO.findById(changeDataUserDTO.getId());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Usuário não encontrado!");
        }
        User user = userOpt.get();
        user.setGender(changeDataUserDTO.getGender());
        user.setName(changeDataUserDTO.getName());
        user.setEmail(changeDataUserDTO.getEmail());
        user.setCpf(changeDataUserDTO.getCpf());

        userDAO.updateForSecretary(user);
        return new MessageDTO("Dados do usuário alterados com sucesso!");
    }

    public MessageDTO updateUserAdmin(UpdateRoleDTO updateRoleDTO) {
        Optional<User> userOpt = userDAO.findByCpf(updateRoleDTO.getCpf());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Usuário não encontrado!");
        }
        if (userOpt.get().getRole().equals(UserRole.valueOf(updateRoleDTO.getRole()))) {
            throw new UserDAOException("A role do usuário já é do tipo " + userOpt.get().getRole().toString(), Response.Status.CONFLICT);
        }
        User user = userOpt.get();
        user.setRole(UserRole.valueOf(updateRoleDTO.getRole()));
        userDAO.updateForAdmin(user);
        return new MessageDTO("Role do usuário alterada com sucesso!");
    }

    public MessageDTO deleteUser(String cpf) {
        userDAO.delete(cpf);
        return new MessageDTO("Usuário deletado com sucesso!");
    }
}
