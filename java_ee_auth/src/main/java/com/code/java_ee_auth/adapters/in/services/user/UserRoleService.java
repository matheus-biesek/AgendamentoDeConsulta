package com.code.java_ee_auth.adapters.in.services.user;

import java.util.List;
import java.util.UUID;

import com.code.java_ee_auth.adapters.out.persistence.UserRoleDAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserRoleService {
    @Inject
    private UserRoleDAO userRoleDAO;

    public void addRoleToUser(UUID userId, String role) {
        userRoleDAO.addRoleToUser(userId, role);
    }

    public void removeRoleFromUser(UUID userId, String role) {
        userRoleDAO.removeRoleFromUser(userId, role);
    }

    public List<String> getRolesByUserId(UUID userId) {
        return userRoleDAO.getRolesByUserId(userId);
    }
}