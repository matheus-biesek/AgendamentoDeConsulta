package com.code.java_ee_workers.adapters.in.security;

import java.util.Map;
import java.util.Set;
import com.jwt.lib.route.AbstractRouteManager;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorkersRouteMnager extends AbstractRouteManager {
    
    @Override
    protected Set<String> getPublicEndpoints() {
        return Set.of(
            "/workers/public"
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.of(
            "/workers/users-by-role", "admin",
            "/workers/user-data", "admin",
            "/patient/create", "secretary",
            "/patient/search-by-id", "secretary,admin",
            "/patient/search-by-user-id", "secretary,admin",
            "/patient/search-by-active", "secretary,admin",
            "/patient/update", "secretary,admin"
        );
    }
}
