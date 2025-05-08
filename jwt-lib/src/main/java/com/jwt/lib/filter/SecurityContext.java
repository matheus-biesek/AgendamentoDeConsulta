package com.jwt.lib.filter;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Interface que define o contexto de segurança específico do usuário.
 * Permite que o usuário defina seu próprio contexto de segurança,
 * incluindo issuer, audience e como configurar o contexto de segurança.
 */
public interface SecurityContext{
    
    /**
     * Obtém o issuer (emissor) do token que é aceito no filtro
     */
    String getIssuer();

    /**
     * Obtém o audience (público) do token que é aceito no filtro
     */
    String getAudience();

    /**
     * Configura o contexto de segurança com base nas claims do token
     */
    void setupContext(ContainerRequestContext ctx, Claims claims);
} 