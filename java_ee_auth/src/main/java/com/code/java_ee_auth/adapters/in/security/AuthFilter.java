package com.code.java_ee_auth.adapters.in.security;

import com.jwt.lib.filter.AbstractJWTAuthFilter;
import com.jwt.lib.filter.SecurityContext;
import com.jwt.lib.route.RouteManager;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class AuthFilter extends AbstractJWTAuthFilter {

    @Inject
    private AuthSecurityContext securityContext;

    @Inject
    private AccessTokenService tokenService;

    @Inject
    private AuthRouterManager routeManager;

    @Override
    protected Object getTokenService() {
        return tokenService;
    }

    @Override
    protected RouteManager getRouteManager() {
        return routeManager;
    }

    @Override
    protected SecurityContext getSecurityContext() {
        return securityContext;
    }
}
