package com.code.java_ee_auth.domain.port.out;

import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.dto.request.UserUpdateDTO;
import java.util.Optional;
import java.util.UUID;

public interface UserDAOPort {
    void create(User user);
    Optional<User> findByCpf(String cpf);
    Optional<User> findById(UUID id);
    void updateUserByCpf(UserUpdateDTO dto);
}
