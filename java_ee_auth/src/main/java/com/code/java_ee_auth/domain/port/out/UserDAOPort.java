package com.code.java_ee_auth.domain.port.out;

import com.code.java_ee_auth.domain.enuns.UserRole;
import com.code.java_ee_auth.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDAOPort {
    void create(User user);
    Optional<User> findByCpf(String cpf);
    void updateForSecretary(User user);
    void updateForAdmin(User user);
    void delete(String cpf);
    Optional<User> findById(UUID id);
    void updateStatus(User user);
    void updateBlocked(User user);
    void updateActive(User user);
    List<User> findByRole(UserRole role);
}
