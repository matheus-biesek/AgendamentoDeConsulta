package com.jwt.lib.example;

import com.jwt.lib.filter.SecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;

public class ExampleSecurityContext implements SecurityContext {
    
    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "auth-service,workers-service,schedules-service";
    }

    @Override
    public void setupContext(ContainerRequestContext ctx, Claims claims) {
        // Aqui o usuário pode configurar seu próprio contexto de segurança
        // Por exemplo, adicionar informações do usuário ao contexto
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        
        // O usuário pode adicionar essas informações ao contexto da requisição
        ctx.setProperty("userId", userId);
        ctx.setProperty("role", role);
        
        // Ou configurar um contexto de segurança específico do framework
        // Exemplo com JAX-RS SecurityContext:
        // ctx.setSecurityContext(new CustomSecurityContext(userId, role));
    }
} 