package com.code.java_ee_schedule.adpters.in.security;

import com.jwt.lib.filter.SecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.List;

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
        System.out.println("Passou aqui - ScheduleSecurityContext");
        String userId = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        
        // Configura o SecurityContext do JAX-RS
        jakarta.ws.rs.core.SecurityContext securityContext = new JaxRsSecurityContextImpl(userId, roles);
        ctx.setSecurityContext(securityContext);
    }
} 