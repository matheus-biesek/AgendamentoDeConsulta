package com.jwt.lib.filter;

import com.jwt.lib.route.RouteManager;
import com.jwt.lib.token.priv.TokenPrivateService;
import com.jwt.lib.token.pub.TokenPublicService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.AUTHENTICATION)
public abstract class AbstractJWTAuthFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJWTAuthFilter.class);

    /**
     * Obtém o serviço de token para validação.
     * O usuário pode implementar este método para retornar um TokenPublicService
     * ou um TokenPrivateService, dependendo de sua necessidade.
     */
    protected abstract Object getTokenService();

    /**
     * Obtém o gerenciador de rotas para controle de acesso
     */
    protected abstract RouteManager getRouteManager();

    /**
     * Obtém o contexto de segurança específico do usuário
     */
    protected abstract SecurityContext getSecurityContext();

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();

        if (getRouteManager().isPublicEndpoint(path)) {
            return;
        }

        String token = extractToken(ctx);
        if (token == null) {
            abort(ctx, "Token não encontrado");
            return;
        }

        try {
            Claims claims = parseToken(token);
            
            if (!validateTokenClaims(claims)) {
                abort(ctx, "Token inválido");
                return;
            }

            String role = claims.get("role", String.class);
            if (!getRouteManager().hasPermission(path, role)) {
                abort(ctx, "Permissão negada");
                return;
            }

            setupSecurityContext(ctx, claims);

        } catch (ExpiredJwtException e) {
            abort(ctx, "Token expirado");
        } catch (Exception e) {
            logger.error("Erro inesperado no filtro de autenticação", e);
            abort(ctx, "Token inválido");
        }
    }

    protected String extractToken(ContainerRequestContext ctx) {
        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    protected Claims parseToken(String token) {
        Object tokenService = getTokenService();
        if (tokenService instanceof TokenPublicService) {
            return ((TokenPublicService) tokenService).parseToken(token);
        } else if (tokenService instanceof TokenPrivateService) {
            return ((TokenPrivateService) tokenService).parseToken(token);
        } else {
            throw new IllegalStateException("O serviço de token deve ser uma instância de TokenPublicService ou TokenPrivateService");
        }
    }

    protected boolean validateTokenClaims(Claims claims) {
        SecurityContext securityContext = getSecurityContext();
        return claims.getIssuer().equals(securityContext.getIssuer()) &&
               claims.getAudience().contains(securityContext.getAudience());
    }

    protected void setupSecurityContext(ContainerRequestContext ctx, Claims claims) {
        SecurityContext securityContext = getSecurityContext();
        securityContext.setupContext(ctx, claims);
    }

    protected void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(message)
                .build());
    }
} 