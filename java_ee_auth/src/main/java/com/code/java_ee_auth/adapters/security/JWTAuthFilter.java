package com.code.java_ee_auth.adapters.security;

import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.rest.RefreshTokenService;
import com.code.java_ee_auth.application.service.security.AccessJWTService;
import com.code.java_ee_auth.domain.enuns.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//É mais interresante voce organizar essa camada para dai depoois adicionar o caso de refresh token.

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    @Inject
    private AccessJWTService accessJWTService;
    @Inject
    private UserDAOImpl userDao;
    @Inject
    private RefreshTokenService refreshTokenService;

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/auth-session/login",
            "/auth-session/register",
            "/auth-session/logout",
            "/auth-session/update-user-secretary",
            "/auth-session/update-user-admin",
            "/auth-session/delete-user",
            "/auth-session/activate-user",
            "/auth-session/register-secretary",
            "/auth-session/register-admin",
            "/auth-front/validate-admin",
            "/auth-front/validate-doctor",
            "/auth-front/validate-nurse",
            "/auth-front/validate-secretary",
            "/auth-front/validate-technician",
            "/auth-front/validate-patient",
            "/auth-session/block-user",
            "/auth-session/unblock-user",
            "/token/refresh");


    private static final Map<String, UserRole> PROTECTED_ENDPOINTS = Map.of(
            "/secure/admin", UserRole.ADMIN,
            "/secure/patient", UserRole.PATIENT,
            "/token/rabbitmq", UserRole.ADMIN,
            "/auth-session/search-user-data", UserRole.ADMIN
    );

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (isPublicEndpoint(path)) {
            return;
        }

        String token = extractToken(requestContext);
        if (token == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token ausente ou inválido").build());
            return;
        }
        try {
            Claims claims = accessJWTService.parseToken(token);
            if ( !claims.getIssuer().equals("auth-service")) {
                System.out.println("Token de access nao condiz com o issuer!");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Usuário inválido").build());
                return;
            }
            UUID userId = UUID.fromString(claims.getSubject());
            UserRole role = UserRole.valueOf(claims.get("role", String.class));

            // Verificar se se o usuario está bloqueado
            if (userDao.findById(userId).get().isBlocked()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Usuário bloqueado").build());
                return;
            }

            // Verificar se se o usuario está ativo
            if (!userDao.findById(userId).get().isActive()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Usuário não está ativo").build());
                return;
            }

            validateCsrfIfNecessary(requestContext, claims);

            SecurityContext securityContext = new CustomSecurityContext(userId, role, requestContext.getSecurityContext());
            requestContext.setSecurityContext(securityContext);

            if (!hasPermission(path, role)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Acesso negado: você não tem permissão para acessar este recurso").build());
            }

        } catch (ExpiredJwtException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"TOKEN_EXPIRED\", \"message\": \"Token expirado. Por favor, faça login novamente.\"}")
                    .build());
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido").build());
        }
    }

    // CRIAR METODO PARA ESTA FUNÇÂO QUE FICARÀ A PARTE, ESTE MESMO METODO È USADO NO REFRESH TOKEN.
    private void validateCsrfIfNecessary(ContainerRequestContext requestContext, Claims claims) {
        String method = requestContext.getMethod();
            boolean isSensitiveMethod = method.equalsIgnoreCase("POST") ||
            method.equalsIgnoreCase("PUT") ||
            method.equalsIgnoreCase("DELETE") ||
            method.equalsIgnoreCase("PATCH");

        if (isSensitiveMethod) {
            String csrfHeader = requestContext.getHeaderString("X-CSRF-TOKEN");
            String csrfClaim = claims.get("csrf", String.class);

            if (csrfHeader == null || !csrfHeader.equals(csrfClaim)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("CSRF token inválido ou ausente").build());
            }
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.contains(path);
    }

    private String extractToken(ContainerRequestContext requestContext) {
        if (requestContext.getCookies().containsKey("token")) {
            return requestContext.getCookies().get("token").getValue();
        }
        return null;
    }

    private boolean hasPermission(String path, UserRole role) {
        return PROTECTED_ENDPOINTS.entrySet().stream()
                .anyMatch(entry -> path.startsWith(entry.getKey()) && role.equals(entry.getValue()));
    }
}
