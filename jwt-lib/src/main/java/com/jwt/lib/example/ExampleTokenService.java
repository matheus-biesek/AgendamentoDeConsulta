package com.jwt.lib.example;

import javax.enterprise.context.ApplicationScoped;

import com.jwt.lib.token.priv.AbstractTokenPrivateService;

@ApplicationScoped
public class ExampleTokenService extends AbstractTokenPrivateService {
    
    @Override
    public String getSecretKey() {
        return "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    }

    @Override
    public long getTokenExpirationMs() {
        return 3600000; // 1 hora em milissegundos
    }

    @Override
    public String getIssuer() {
        return "auth-service";
    }

    @Override
    public String getAudience() {
        return "auth-service,workers-service,schedules-service";
    }
} 