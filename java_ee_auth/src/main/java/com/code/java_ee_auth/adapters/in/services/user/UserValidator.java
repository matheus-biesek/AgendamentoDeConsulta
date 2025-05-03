package com.code.java_ee_auth.adapters.in.services.user;

import com.code.java_ee_auth.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserValidator {
    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    public static boolean validateUser(Optional<User> user) {
        if (user.isEmpty()) {
            logger.warn("Usuario nao encontrado!");
            return false;
        }
        if (!user.get().isActive()) {
            logger.warn("Usuario nao esta ativo!");
            return false;
        }
        if (user.get().isBlocked()) {
            logger.warn("Usuario bloqueado!");
            return false;
        }
        return true;
    }
} 