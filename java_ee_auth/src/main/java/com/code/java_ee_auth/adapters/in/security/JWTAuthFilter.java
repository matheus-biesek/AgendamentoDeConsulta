package com.code.java_ee_auth.adapters.in.security;

import com.code.java_ee_auth.adapters.in.services.security.AccessTokenService;
import com.code.java_ee_auth.adapters.utils.CsrfTokenChecker;
import com.code.java_ee_auth.domain.enuns.AuthError;
import com.code.java_ee_auth.domain.enuns.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class JWTAuthFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(JWTAuthFilter.class.getName());

    // na biblioteca será adicionado variaveis genericas, quando o usuario implementar a classe AccessTokenService, ele terá que passar as variaveis genericas.
    private static final String ISSUER = "auth-service";
    private static final String AUDIENCE = "auth-service";

    @Inject
    private RequestInfoService requestInfoService;

    @Inject
    private AccessTokenService accessTokenService;

    @Inject
    private RouteManager routeManager;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();

        // Verifica se é uma rota pública
        if (routeManager.isPublicEndpoint(path)) {
            return;
        }

        // coleta o token JWT
        String token = requestInfoService.extractCookieValue(ctx, "accessToken");
        if (token == null) {
            abort(ctx, AuthError.TOKEN_MISSING.getMessage());
            return;
        }

        try {
            Claims claims = accessTokenService.parseToken(token);
            
            // Validação do issuer e audience
            if (!claims.getIssuer().equals(ISSUER) || !claims.getAudience().contains(AUDIENCE)) {
                abort(ctx, AuthError.TOKEN_INVALID.getMessage());
                return;
            }

            // Validação do CSRF token
            CsrfTokenChecker.validate(ctx, claims);
            
            UserRole role = UserRole.valueOf(claims.get("role", String.class));
            if (!routeManager.hasPermission(path, role)) {
                abort(ctx, AuthError.PERMISSION_DENIED.getMessage());
                return;
            }

            // Configuração do contexto de segurança
            UUID userId = UUID.fromString(claims.getSubject());
            SecurityContext securityContext = new CustomSecurityContext(userId, role, ctx.getSecurityContext());
            ctx.setSecurityContext(securityContext);

        } catch (ExpiredJwtException e) {
            abort(ctx, AuthError.TOKEN_EXPIRED.getMessage());
            return;

        } catch (Exception e) {
            logger.severe("Erro inesperado no filtro de autenticação: " + e.getMessage() + "\n" + e.getStackTrace());
            abort(ctx, AuthError.TOKEN_INVALID.getMessage());
            return;
        }
    }

    protected void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(message).build());
    }
}
