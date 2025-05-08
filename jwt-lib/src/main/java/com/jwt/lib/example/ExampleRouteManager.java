package com.jwt.lib.example;

import com.jwt.lib.route.AbstractRouteManager;
import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ExampleRouteManager extends AbstractRouteManager {
    
    @Override
    protected Set<String> getPublicEndpoints() {
        return Set.of(
            "/auth/login",
            "/auth/register",
            "/health"
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.of(
            "/admin", "ADMIN",
            "/user", "USER",
            "/worker", "WORKER"
        );
    }
} 