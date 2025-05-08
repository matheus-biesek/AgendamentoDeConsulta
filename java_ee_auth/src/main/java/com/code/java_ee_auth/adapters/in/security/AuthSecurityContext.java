package com.code.java_ee_auth.adapters.in.security;

import com.jwt.lib.filter.SecurityContext;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthSecurityContext implements SecurityContext{

    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "workers-service";
    }

    @Override
    public void setupContext(ContainerRequestContext ctx, Claims claims) {
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        
        ctx.setProperty("userId", userId);
        ctx.setProperty("role", role);
    }
}
