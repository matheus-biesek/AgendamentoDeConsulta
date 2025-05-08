package com.code.java_ee_workers.adapters.in.security;

import com.jwt.lib.token.pub.AbstractTokenPublicService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenService extends AbstractTokenPublicService {

    @Override
    public String getSecretKey() {
        return "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    }
    
}
