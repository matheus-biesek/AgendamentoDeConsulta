package com.jwt.lib.route;

import java.util.Set;
import java.util.Map;

public abstract class AbstractRouteManager implements RouteManager {
    
    protected abstract Set<String> getPublicEndpoints();
    protected abstract Map<String, String> getProtectedEndpoints();

    @Override
    public boolean isPublicEndpoint(String path) {
        return getPublicEndpoints().contains(path);
    }

    @Override
    public boolean hasPermission(String path, String role) {
        return getProtectedEndpoints().entrySet().stream()
            .anyMatch(entry -> path.startsWith(entry.getKey()) && role.equals(entry.getValue()));
    }
} 