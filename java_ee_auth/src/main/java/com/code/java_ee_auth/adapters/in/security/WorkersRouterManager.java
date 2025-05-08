package com.code.java_ee_auth.adapters.in.security;

import java.util.Map;
import java.util.Set;

import com.jwt.lib.route.AbstractRouteManager;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorkersRouterManager extends AbstractRouteManager {
    
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
            Map.entry("/auth-front/validate-admin", "ADMIN"),
            Map.entry("/auth-front/validate-secretary", "SECRETARY"),
            Map.entry("/auth-front/validate-technician", "TECHNICIAN"),
            Map.entry("/auth-front/validate-patient", "PATIENT"),
            Map.entry("/auth-front/validate-doctor", "DOCTOR"),
            Map.entry("/auth-front/validate-nurse", "NURSE"),
            Map.entry("/user-management/register-secretary", "SECRETARY"),
            Map.entry("/user-management/register-admin", "ADMIN"),
            Map.entry("/user-management/search-user-data", "ADMIN"),
            Map.entry("/user-management/delete-user", "ADMIN"),
            Map.entry("/user-management/activate-user", "SECRETARY"),
            Map.entry("/user-management/update-user-secretary", "SECRETARY"),
            Map.entry("/user-management/update-user-admin", "ADMIN"),
            Map.entry("/user-management/block-user", "ADMIN"),
            Map.entry("/user-management/unblock-user", "ADMIN"),
            Map.entry("/secure/admin", "ADMIN"),
            Map.entry("/secure/patient", "PATIENT")
        );
    }
}
