package com.code.java_ee_auth.adapters.in.services.user;

import com.code.java_ee_auth.domain.model.User;
import java.util.Optional;

public class UserValidator {

    public static void validateUser(Optional<User> user) {
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario nao encontrado!");
        }
        if (!user.get().isActive()) {
            throw new RuntimeException("Usuario nao esta ativo!");
        }
        if (user.get().isBlocked()) {
            throw new RuntimeException("Usuario bloqueado!");
        }
    }
} 