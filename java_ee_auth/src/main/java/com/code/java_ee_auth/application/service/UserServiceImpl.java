package com.code.java_ee_auth.application.service;

import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.rest.exeception.UserDAOException;
import com.code.java_ee_auth.adapters.rest.exeception.UserNoExistException;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.request.UsernameDTO;
import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
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

    public MessageDTO createUser(UserInfoDTO userInfo) {
        User user = new User(userInfo.getPassword(), userInfo.getUsername(), userInfo.getRole(), userInfo.getSex());
        userDAO.create(user);
        return new MessageDTO("Usuário criado com sucesso");
    }

    public UserDataDTO getUserData(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNoExistException("Usuário não encontrado!");
        }
        return new UserDataDTO(user.get().getId(), user.get().getUsername(), user.get().getRole(), user.get().getSex());
    }

    public MessageDTO updateUserRole(UpdateRoleDTO updateRoleDTO) {
        Optional<User> userOpt = userDAO.findByUsername(updateRoleDTO.getUsername());
        if (userOpt.isEmpty()) {
            throw new UserNoExistException("Usuário não encontrado!");
        }
        if (userOpt.get().getRole().equals(updateRoleDTO.getRole())) {
            throw new UserDAOException("A role do usuário já é do tipo " + userOpt.get().getRole().toString(), Response.Status.CONFLICT);
        }
        User user = userOpt.get();
        user.setRole(updateRoleDTO.getRole());
        userDAO.update(user);
        return new MessageDTO("Role do usuário alterada com sucesso!");
    }

    public MessageDTO deleteUser(UsernameDTO usernameDTO) {
        userDAO.delete(usernameDTO.getUsername());
        return new MessageDTO("Usuário deletado com sucesso!");
    }
}
