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
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.ofEntries(
            // Room
            Map.entry("/admin/private", "admin"),
            Map.entry("/room/create", "admin"),
            Map.entry("/room/update", "admin"),
            Map.entry("/room/search-by-user-id", "admin"),
            Map.entry("/room/search-all", "admin"),
            // Schedule Template
            Map.entry("/schedule-template/create", "admin"),
            Map.entry("/schedule-template/update", "admin"),
            Map.entry("/schedule-template/find-all", "admin"),
            // Time Slot
            Map.entry("/time-slot/create", "admin"),
            Map.entry("/time-slot/find-all-by-user-id", "admin"),
            Map.entry("/time-slot/cancel-by-id", "admin"),
            // Appointment
            Map.entry("/appointment/create", "secretary"),
            Map.entry("/appointment/cancel", "secretary"),
            Map.entry("/appointment/find-all", "admin,secretary")
        );
    }

}
