package com.code.java_ee_auth.adapters.in.security;

import com.code.java_ee_auth.domain.enuns.UserRole;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.code.java_ee_auth.domain.port.in.RouteManagerPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RouteManager implements RouteManagerPort {
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "/auth-session/login",
        "/auth-session/logout",
        "/token/refresh");

    private static final Map<String, UserRole> PROTECTED_ENDPOINTS = new HashMap<>();

    static {
        PROTECTED_ENDPOINTS.put("/auth-front/validate-admin", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/auth-front/validate-secretary", UserRole.SECRETARY);
        PROTECTED_ENDPOINTS.put("/auth-front/validate-technician", UserRole.TECHNICIAN);
        PROTECTED_ENDPOINTS.put("/auth-front/validate-patient", UserRole.PATIENT);
        PROTECTED_ENDPOINTS.put("/auth-front/validate-doctor", UserRole.DOCTOR);
        PROTECTED_ENDPOINTS.put("/auth-front/validate-nurse", UserRole.NURSE);
        PROTECTED_ENDPOINTS.put("/user-management/register-secretary", UserRole.SECRETARY);
        PROTECTED_ENDPOINTS.put("/user-management/register-admin", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/user-management/search-user-data", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/user-management/delete-user", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/user-management/activate-user", UserRole.SECRETARY);
        PROTECTED_ENDPOINTS.put("/user-management/update-user-secretary", UserRole.SECRETARY);
        PROTECTED_ENDPOINTS.put("/user-management/update-user-admin", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/user-management/block-user", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/user-management/unblock-user", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/secure/admin", UserRole.ADMIN);
        PROTECTED_ENDPOINTS.put("/secure/patient", UserRole.PATIENT);
    }
        
    @Override
    public boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.contains(path);
    }

    @Override
    public boolean hasPermission(String path, UserRole role) {
        return PROTECTED_ENDPOINTS.entrySet().stream()
            .anyMatch(entry -> path.startsWith(entry.getKey()) && role.equals(entry.getValue()));
    }

}
