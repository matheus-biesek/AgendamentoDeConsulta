package com.code.java_ee_schedule.adpters.in.security;

import jakarta.ws.rs.core.SecurityContext;
import java.util.UUID;

public class SecurityUtils {
    
    public static UUID getUserId(SecurityContext securityContext) {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return UUID.fromString(securityContext.getUserPrincipal().getName());
    }
} 