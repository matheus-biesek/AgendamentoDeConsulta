package com.jwt.lib.example;

import com.jwt.lib.filter.SecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.List;

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
        String userId = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        
        ctx.setProperty("userId", userId);
        ctx.setProperty("roles", roles);
        
        // O usuário pode configurar seu próprio contexto de segurança aqui
        // Por exemplo, usando JAX-RS SecurityContext:
        // ctx.setSecurityContext(new CustomSecurityContext(userId, roles));
    }
}
