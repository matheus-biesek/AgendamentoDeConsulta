package com.jwt.lib.example;

import com.jwt.lib.filter.AbstractJWTAuthFilter;
import com.jwt.lib.filter.SecurityContext;
import com.jwt.lib.route.RouteManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ExampleJWTAuthFilter extends AbstractJWTAuthFilter {

    @Inject
    private ExampleTokenService tokenService;

    @Inject
    private ExampleRouteManager routeManager;

    @Inject
    private ExampleSecurityContext securityContext;

    @Override
    protected Object getTokenService() {
        // Aqui o usuário pode retornar um TokenPublicService ou TokenPrivateService
        // dependendo de sua necessidade
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