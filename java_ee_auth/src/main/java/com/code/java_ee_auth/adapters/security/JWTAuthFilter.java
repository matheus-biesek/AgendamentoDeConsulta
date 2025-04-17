package com.code.java_ee_auth.adapters.security;

import com.code.java_ee_auth.adapters.security.jwt.JWTUtils;
import com.code.java_ee_auth.domain.enuns.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/auth-session/login",
            "/auth-session/register",
            "/auth-session/logout",
            "/user/create",
            "/auth-front/validate-admin",
            "/auth-front/validate-doctor",
            "/auth-front/validate-nurse",
            "/auth-front/validate-secretary",
            "/auth-front/validate-technician",
            "/auth-front/validate-patient");

    private static final Map<String, UserRole> PROTECTED_ENDPOINTS = Map.of(
            "/secure/admin", UserRole.ADMIN,
            "/secure/patient", UserRole.PATIENT,
            "/token/rabbitmq", UserRole.ADMIN
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
            Claims claims = JWTUtils.parseToken(token);
            String username = claims.getSubject();
            UserRole role = UserRole.valueOf(claims.get("role", String.class));

            validateCsrfIfNecessary(requestContext, claims);

            SecurityContext securityContext = new CustomSecurityContext(username, role, requestContext.getSecurityContext());
            requestContext.setSecurityContext(securityContext);

            if (!hasPermission(path, role)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Acesso negado: você não tem permissão para acessar este recurso").build());
            }

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido").build());
        }
    }

    private void validateCsrfIfNecessary(ContainerRequestContext requestContext, Claims claims) {
        String method = requestContext.getMethod();
        boolean isSensitiveMethod = method.equalsIgnoreCase("POST") ||
                method.equalsIgnoreCase("PUT") ||
                method.equalsIgnoreCase("DELETE");

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
