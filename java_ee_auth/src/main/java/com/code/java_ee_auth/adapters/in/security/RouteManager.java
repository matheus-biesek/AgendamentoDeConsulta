package com.code.java_ee_auth.adapters.in.security;

import com.code.java_ee_auth.domain.enuns.UserRole;
import java.util.Map;
import java.util.Set;

public class RouteManager {
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "/auth-session/login",
        "/auth-session/register",
        "/auth-session/logout",
        "/auth-session/update-user-secretary",
        "/auth-session/update-user-admin",
        "/auth-session/delete-user",
        "/auth-session/activate-user",
        "/auth-session/register-secretary",
        "/auth-session/register-admin",
        "/auth-session/block-user",
        "/auth-session/unblock-user",
        "/token/refresh");

    private static final Map<String, UserRole> PROTECTED_ENDPOINTS = Map.of(
        "/auth-front/validate-admin", UserRole.ADMIN,
        "/auth-front/validate-secretary", UserRole.SECRETARY,
        "/auth-front/validate-technician", UserRole.TECHNICIAN,
        "/auth-front/validate-patient", UserRole.PATIENT,
        "/auth-front/validate-doctor", UserRole.DOCTOR,
        "/auth-front/validate-nurse", UserRole.NURSE,
        "/secure/admin", UserRole.ADMIN,
        "/secure/patient", UserRole.PATIENT,
        "/token/rabbitmq", UserRole.ADMIN,
        "/auth-session/search-user-data", UserRole.ADMIN
    );

    public static boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.contains(path);
    }

    public static boolean hasPermission(String path, UserRole role) {
        return PROTECTED_ENDPOINTS.entrySet().stream()
            .anyMatch(entry -> path.startsWith(entry.getKey()) && role.equals(entry.getValue()));
    }

}
