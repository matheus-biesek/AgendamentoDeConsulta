package com.jwt.lib.route;

public interface RouteManager {
    boolean isPublicEndpoint(String path);
    boolean hasPermission(String path, String role);
} 