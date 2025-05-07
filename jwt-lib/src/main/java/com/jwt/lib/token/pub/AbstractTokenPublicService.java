package com.jwt.lib.token.pub;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public abstract class AbstractTokenPublicService implements TokenPublicService {
    
    @Override
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(getSecretKey());
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
} 