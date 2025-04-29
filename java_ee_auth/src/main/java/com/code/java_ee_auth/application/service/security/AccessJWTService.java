package com.code.java_ee_auth.application.service.security;

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
public class AccessJWTService {

    // Adicione a chave secreta aqui
    private static final String SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 1 minutos em milissegundos

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

    public String generateToken(UUID userId, String role, String csrfToken) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .claim("csrf", csrfToken)
                .setIssuedAt(new Date())
                .setExpiration(new Date((long) (System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Futuramente apaguar este método pois está aplicação não precissará de validar o token
    public boolean validateTokenRole(String token, UserRole expectedRole) {
        try {
            Claims claims = parseToken(token);
            String roleString = claims.get("role", String.class);  // Obtém a role como String

            if (roleString == null) {
                return false;
            }

            UserRole role = UserRole.valueOf(roleString.toUpperCase());
            return role == expectedRole;

        } catch (Exception e) {
            return false;
        }
    }
}
