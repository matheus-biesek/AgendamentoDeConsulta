package com.code.java_ee_schedule.adpters.in.security;

import com.jwt.lib.route.AbstractRouteManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ScheduleRouteManager extends AbstractRouteManager {
    
    @Override
    protected Set<String> getPublicEndpoints() {
        return Set.of(
            "/admin/public"
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.of(
            "/admin/private", "admin"
        );
    }

}
