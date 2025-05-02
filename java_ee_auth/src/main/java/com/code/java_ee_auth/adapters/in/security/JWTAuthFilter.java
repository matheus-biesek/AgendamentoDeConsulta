package com.code.java_ee_auth.adapters.in.security;

import com.code.java_ee_auth.adapters.in.services.RequestInfoService;
import com.code.java_ee_auth.adapters.in.services.security.AccessTokenService;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.utils.CsrfTokenChecker;
import com.code.java_ee_auth.domain.enuns.AuthError;
import com.code.java_ee_auth.domain.model.User;
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
import java.util.UUID;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(JWTAuthFilter.class.getName());

    @Inject
    private RequestInfoService requestInfoService;
    @Inject
    private AccessTokenService accessTokenService;
    @Inject
    private UserDAOImpl userDao;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();


        if (RouteManager.isPublicEndpoint(path)) {
            return;
        }

        String token = requestInfoService.extractCookieValue(ctx, "token");

        if (token == null) {
            abort(ctx, AuthError.TOKEN_MISSING.getMessage());
            return;
        }

        try {
            Claims claims = accessTokenService.parseToken(token);
            if (!claims.getIssuer().equals("auth-service")) {
                abort(ctx, AuthError.TOKEN_INVALID.getMessage());
                return;
            }
            CsrfTokenChecker.validate(ctx, claims);

            UUID userId = UUID.fromString(claims.getSubject());
            User user = userDao.findById(userId).get();


            if (!RouteManager.hasPermission(path, user.getRole())) {
                abort(ctx, AuthError.PERMISSION_DENIED.getMessage());
                return;
            }

            if (!validateUserState(user, ctx)) {
                return;
            }

            SecurityContext securityContext = new CustomSecurityContext(userId, user.getRole(), ctx.getSecurityContext());
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

    private boolean validateUserState(User user, ContainerRequestContext ctx) {
        if (user.isBlocked()) {
            abort(ctx, AuthError.USER_BLOCKED.getMessage());
            return false;
        }

        if (!user.isActive()) {
            abort(ctx, AuthError.USER_NOT_ACTIVE.getMessage());
            return false;
        }

        return true;
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(message).build());
        return;
    }   
}
