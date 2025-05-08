package com.jwt.lib.token.pub;

import io.jsonwebtoken.Claims;

public interface TokenPublicService {
    Claims parseToken(String token);
    String getSecretKey();
} 