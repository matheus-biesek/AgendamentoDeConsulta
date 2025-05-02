package com.code.java_ee_auth.adapters.utils;

import java.util.Set;

import com.code.java_ee_auth.domain.enuns.AuthError;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

public class CsrfTokenChecker {

    public static void validate(ContainerRequestContext requestContext, Claims claims) {
        String method = requestContext.getMethod();
        boolean isSensitive = Set.of("POST", "PUT", "DELETE", "PATCH").contains(method);

        if (isSensitive) {
            String csrfHeader = requestContext.getHeaderString("X-CSRF-TOKEN");
            String csrfClaim = claims.get("csrf", String.class);

            if (csrfHeader == null || !csrfHeader.equals(csrfClaim)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity(AuthError.CSRF_TOKEN_INVALID.getMessage()).build());
                return;
            }
        }
    }
}

