package com.code.java_ee_auth.adapters.security;

import com.code.java_ee_auth.domain.enuns.UserRole;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.UUID;
public class CustomSecurityContext implements SecurityContext {
    private final UUID userId;
    private final UserRole role;
    private final SecurityContext originalSecurityContext;

    public CustomSecurityContext(UUID userId, UserRole role, SecurityContext originalSecurityContext) {
        this.userId = userId;
        this.role = role;
        this.originalSecurityContext = originalSecurityContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> userId.toString();
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.role.name().equalsIgnoreCase(role);
    }

    @Override
    public boolean isSecure() {
        return originalSecurityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return originalSecurityContext.getAuthenticationScheme();
    }
}
