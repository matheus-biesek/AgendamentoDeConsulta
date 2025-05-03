package com.code.java_ee_auth.domain.port.in;

import com.code.java_ee_auth.domain.enuns.UserRole;

public interface RouteManagerPort {
    boolean isPublicEndpoint(String path);
    boolean hasPermission(String path, UserRole role);
}
