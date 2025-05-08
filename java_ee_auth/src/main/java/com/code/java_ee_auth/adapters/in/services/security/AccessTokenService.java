package com.code.java_ee_auth.adapters.in.services.security;

import com.code.java_ee_auth.domain.enuns.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccessTokenService {

    // Adicione a chave secreta aqui via env
    private static final String SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1 * 60 * 1000; // 1 minutos em milissegundos
    private static final String ISSUER = "auth-service";
    private static final String AUDIENCE = "auth-service,workers-service,schedules-service,consumer-status-appointment,consumer-change-password";
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UUID userId, String csrfToken, UserRole role) {       
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer(ISSUER)
            .setAudience(AUDIENCE)
            .claim("csrf", csrfToken)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date((long) (System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))) 
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
