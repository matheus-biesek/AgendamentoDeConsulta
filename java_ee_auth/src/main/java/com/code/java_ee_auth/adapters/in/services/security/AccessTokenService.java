package com.code.java_ee_auth.adapters.in.services.security;

import com.jwt.lib.token.priv.AbstractTokenPrivateService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccessTokenService extends AbstractTokenPrivateService {

    @Override
    public String getSecretKey() {
        return "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    }

    @Override
    public long getTokenExpirationMs() {
        return 1 * 60 * 1000;
    }

    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "auth-service,workers-service,schedules-service,consumer-status-appointment,consumer-change-password";
    }
}
