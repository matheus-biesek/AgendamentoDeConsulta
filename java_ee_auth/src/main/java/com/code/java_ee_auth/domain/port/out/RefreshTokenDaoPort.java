package com.code.java_ee_auth.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.code.java_ee_auth.domain.dto.request.RefreshTokenUpdateDTO;
import com.code.java_ee_auth.domain.model.RefreshToken;

public interface RefreshTokenDaoPort {
    boolean create(RefreshToken refreshToken);
    List<RefreshToken> findAllByUserId(UUID userId, boolean active);
    void update(RefreshTokenUpdateDTO dto);
    RefreshToken findById(UUID refreshTokenId);
    boolean validateOwnership(UUID refreshTokenId, UUID userId);
}
