package com.code.java_ee_auth.adapters.in.services.token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.code.java_ee_auth.adapters.in.security.AccessTokenService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthAccessTokenService {

    @Inject
    private AccessTokenService accessTokenService;

    public Map<String, String> createWithCsrf(String userId, List<String> roles) {
        String csrfToken = UUID.randomUUID().toString();
    
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("csrf", csrfToken);
    
        String token = accessTokenService.generateToken(userId, claims);
    
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", token);
        result.put("csrfToken", csrfToken);
        return result;
    }
    
}
