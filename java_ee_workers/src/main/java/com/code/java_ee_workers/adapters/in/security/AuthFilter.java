package com.code.java_ee_workers.adapters.in.security;

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
public class AuthFilter extends AbstractJWTAuthFilter{

    @Inject
    private TokenService tokenService;

    @Inject
    private WorkersRouteMnager routeManager;

    @Inject
    private WorkersSecurityContext securityContext;

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

    @Override
    protected String getCsrfHeaderName() {
        return "X-CSRF-TOKEN";
    }

    @Override
    protected String getCsrfClaimName() {
        return "csrf";
    }
    
}
