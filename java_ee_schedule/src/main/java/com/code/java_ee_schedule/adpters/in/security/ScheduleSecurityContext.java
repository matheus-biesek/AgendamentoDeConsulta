package com.code.java_ee_schedule.adpters.in.security;

import com.jwt.lib.filter.SecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

@ApplicationScoped
public class ScheduleSecurityContext implements SecurityContext {
    
    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "schedules-service";
    }

    @Override
    public void setupContext(ContainerRequestContext ctx, Claims claims) {
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        
        // Configura o contexto de segurança
        ctx.setProperty("userId", userId);
        ctx.setProperty("role", role);
    }
} 