package com.code.java_ee_auth.domain.port.out;

import com.code.java_ee_auth.domain.model.User;
import java.util.Optional;

public interface UserDAOPort {
    Optional<User> findByUsername(String username);
    void create(User newUser);
    void update(User user);
    void delete (String username);
}
