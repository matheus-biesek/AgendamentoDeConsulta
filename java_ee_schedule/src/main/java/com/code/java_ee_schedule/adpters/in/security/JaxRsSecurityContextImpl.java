package com.code.java_ee_schedule.adpters.in.security;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

public class JaxRsSecurityContextImpl implements SecurityContext {
    private final String userId;
    private final List<String> roles;

    public JaxRsSecurityContextImpl(String userId, List<String> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> userId;
    }

    @Override
    public boolean isUserInRole(String role) {
        return roles != null && roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
} 