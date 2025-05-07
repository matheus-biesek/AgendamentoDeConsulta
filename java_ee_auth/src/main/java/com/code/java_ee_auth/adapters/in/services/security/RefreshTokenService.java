package com.code.java_ee_auth.adapters.in.services.security;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenService {
    
    // Adicione a chave secreta aqui
    private static final String SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000 * 60 * 60 * 7; // 7 horas

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
    
    public String generateToken(UUID userId, UUID refreshTokenId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer("auth-service")
                .setAudience("workers-service,schedules-service,consumer-status-appointment,consumer-change-password")
                .claim("tokenType", "refresh")
                .claim("refreshTokenId", refreshTokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
