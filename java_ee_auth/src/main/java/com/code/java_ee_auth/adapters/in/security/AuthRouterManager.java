package com.code.java_ee_auth.adapters.in.security;

import java.util.Map;
import java.util.Set;

import com.jwt.lib.route.AbstractRouteManager;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRouterManager extends AbstractRouteManager {
    
    @Override
    protected Set<String> getPublicEndpoints() {
        return Set.of(
            "/auth-session/login",
            "/auth-session/logout",
            "/token/refresh"
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.ofEntries(
            Map.entry("/auth-front/validate-admin", "admin"),
            Map.entry("/auth-front/validate-secretary", "secretary"),
            Map.entry("/auth-front/validate-patient", "patient"),
            Map.entry("/auth-front/validate-professional", "doctor,nurse,secretary,technician,admin"),
            // --------------------------------------------------------------------
            Map.entry("/user-management/register-user", "secretary,admin"),
            Map.entry("/user-management/search-user-data", "admin"),
            Map.entry("/user-management/delete-or-activate-user", "admin,secretary"),
            Map.entry("/user-management/update-user-data", "secretary,admin"),
            Map.entry("/user-management/add-role-to-user", "admin"),
            Map.entry("/user-management/remove-role-from-user", "admin"),
            Map.entry("/user-management/block-or-unblock-user", "admin"),
            Map.entry("/user-management/get-users-by-role-and-active", "admin"),
            Map.entry("/secure/admin", "admin"),
            Map.entry("/secure/patient", "patient")
        );
    }
}
