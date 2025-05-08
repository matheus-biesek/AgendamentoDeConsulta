package com.code.java_ee_auth.adapters.in.services.security;

import com.jwt.lib.token.priv.AbstractTokenPrivateService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenService extends AbstractTokenPrivateService {

    @Override
    public String getSecretKey() {
        return "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    }

    @Override
    public long getTokenExpirationMs() {
        return 1000 * 60 * 60 * 7; // 7 horas
    }

    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "auth-service";
    }
}
