package com.jwt.lib.filter;

import com.jwt.lib.route.RouteManager;
import com.jwt.lib.token.priv.TokenPrivateService;
import com.jwt.lib.token.pub.TokenPublicService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public abstract class AbstractJWTAuthFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJWTAuthFilter.class);
    private static final Jsonb jsonb = JsonbBuilder.create();

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

    // Biblioteca atual não suporta o uso de CSRF, por isso não é possível usar o método filter
    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();

        if (getRouteManager().isPublicEndpoint(path)) {
            return;
        }

        String token = extractCookieValue(ctx, "accessToken");
        if (token == null) {
            abort(ctx, "TOKEN_MISSING", "Token não encontrado", false, null);
            return;
        }

        try {
            Claims claims = parseToken(token);
            
            if (!validateTokenClaims(claims)) {
                abort(ctx, "TOKEN_INVALID", "Token inválido", false, null);
                return;
            }

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                abort(ctx, "TOKEN_INVALID", "Token não possui roles", false, null);
                return;
            }
            logger.info("Roles: {}", roles);

            boolean hasPermission = roles.stream()
                .anyMatch(role -> getRouteManager().hasPermission(path, role));

            if (!hasPermission) {
                abort(ctx, "PERMISSION_DENIED", "Permissão negada", false, null);
                return;
            }

            setupSecurityContext(ctx, claims);

        } catch (ExpiredJwtException e) {
            abort(ctx, "TOKEN_EXPIRED", "Token expirado", true, "/rest-auth/token/refresh");
        } catch (Exception e) {
            logger.error("Erro inesperado no filtro de autenticação", e);
            abort(ctx, "TOKEN_INVALID", "Token inválido", false, null);
        }
    }

    protected String extractCookieValue(ContainerRequestContext ctx, String cookieName) {
        Cookie cookie = ctx.getCookies().get(cookieName);
        return cookie != null ? cookie.getValue() : null;
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

    protected void abort(ContainerRequestContext ctx, String error, String message, boolean shouldRefresh, String refreshEndpoint) {
        ErrorResponse errorResponse = new ErrorResponse(error, message, shouldRefresh, refreshEndpoint);
        String jsonResponse = jsonb.toJson(errorResponse);
        
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
            .entity(jsonResponse)
            .type(MediaType.APPLICATION_JSON)
            .build());
    }
}
