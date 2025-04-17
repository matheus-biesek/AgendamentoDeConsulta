package com.code.java_ee_auth.adapters.security.jwt;

import com.code.java_ee_auth.domain.enuns.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JWTUtils {
    private static final String SECRET_KEY = "ewoCu39mGULU1oFgkIoy6Z2OEjvXd4Y1jzL/p60Xu1I=";

    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String generateToken(String username, String role, String csrfToken) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("csrf", csrfToken)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ((60 * 60) * 60000))) // 6 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateTokenRole(String token, UserRole expectedRole) {
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
