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
            Map.entry("/room/search", "admin"),
            Map.entry("/room/create", "admin"),
            Map.entry("/room/update", "admin"),
            Map.entry("/room/search-all", "admin"),
            // Schedule Template
            Map.entry("/schedule-template/create", "admin"),
            Map.entry("/schedule-template/update", "admin"),
            Map.entry("/schedule-template/find-by-user-id", "admin"),
            Map.entry("/schedule-template/find-by-id", "admin"),
            // Time Slot
            Map.entry("/time-slot/create", "admin"),
            Map.entry("/time-slot/find-all-by-user-id", "admin"),
            Map.entry("/time-slot/cancel-by-id", "admin"),
            Map.entry("/time-slot/my-time-slots", "secretary,doctor,technician,secretary,admin,nurse"),
            // Appointment
            Map.entry("/appointment/create", "secretary"),
            Map.entry("/appointment/cancel", "secretary"),
            Map.entry("/appointment/find-all", "admin,secretary"),
            Map.entry("/appointment/find-by-user-id", "secretary,admin"),
            Map.entry("/appointment/my-appointments", "patient"),
            Map.entry("/appointment/find-by-professional-id", "secretary,doctor,technician,secretary,admin,nurse")

        );
    }

}
