package com.code.java_ee_auth.adapters.in.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;

@ApplicationScoped
public class RequestInfoService {

    public String extractCookieValue(ContainerRequestContext ctx, String cookieName) {
        Cookie cookie = ctx.getCookies().get(cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
    
}
