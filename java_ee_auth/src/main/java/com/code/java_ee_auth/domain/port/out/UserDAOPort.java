package com.code.java_ee_auth.domain.port.out;

import com.code.java_ee_auth.domain.model.User;
import java.util.Optional;

public interface UserDAOPort {
    Optional<User> findByCpf(String cpf);
    void create(User newUser);
    void updateForSecretary(User user);
    void updateForAdmin(User user);
    void delete (String cpf);
}
