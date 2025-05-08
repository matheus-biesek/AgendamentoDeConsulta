package com.jwt.lib.token.priv;

import io.jsonwebtoken.Claims;
import java.util.Map;

public interface TokenPrivateService {
    String generateToken(String subject, Map<String, Object> claims);
    Claims parseToken(String token);
    String getSecretKey();
    long getTokenExpirationMs();
    String getIssuer();
    String getAudience();
} 