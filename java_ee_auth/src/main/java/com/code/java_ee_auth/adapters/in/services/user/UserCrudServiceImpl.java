package com.code.java_ee_auth.adapters.in.services.user;

import com.code.java_ee_auth.adapters.in.services.session.PasswordService;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.request.UserUpdateDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import com.code.java_ee_auth.domain.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserCrudServiceImpl {

    @Inject
    private UserDAOImpl userDAO;

    @Inject
    private PasswordService passwordService;

    @Inject
    private UserRoleService userRoleService;

    public void create(UserInfoDTO dto, String role) {
        if (role == null) {
            role = "patient";
        }
        Optional<User> userExist = userDAO.findByCpf(dto.getCpf());
        if (userExist.isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }
        String hashedPassword = passwordService.hash(dto.getPassword());
        UUID id = UUID.randomUUID();
        User user = new User(id, dto.getName(), dto.getCpf(), dto.getEmail(), hashedPassword, dto.getGender());
        userDAO.create(user);
        userRoleService.addRoleToUser(id, role);
    }

    public UserDataDTO getUserData(String cpf) {
        Optional<User> user = userDAO.findByCpf(cpf);
        if (user.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado!");
        }
        List<String> roles = userRoleService.getRolesByUserId(user.get().getId());
        return new UserDataDTO(user.get().getId(), user.get().isBlocked(), user.get().isActive(), user.get().getName(), user.get().getCpf(), user.get().getEmail(), user.get().getGender(), roles);
    }

    public void updateByCpf(UserUpdateDTO userUpdateDTO) {
        userDAO.updateUserByCpf(userUpdateDTO);
    }

    public List<UserDataDTO> getUsersByRole(String role) {
        List<User> users = userDAO.findAll();
        return users.stream()
            .filter(user -> {
                List<String> userRoles = userRoleService.getRolesByUserId(user.getId());
                return userRoles.contains(role);
            })
            .map(user -> new UserDataDTO(
                user.getId(),
                user.isBlocked(),
                user.isActive(),
                user.getName(),
                user.getCpf(),
                user.getEmail(),
                user.getGender(),
                userRoleService.getRolesByUserId(user.getId())
            ))
            .toList();
    }
}
