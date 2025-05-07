package com.code.java_ee_auth.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.code.java_ee_auth.domain.model.RefreshToken;

public interface RefreshTokenDaoPort {
    boolean saveRefreshToken(RefreshToken refreshToken);
    List<RefreshToken> findRefreshTokensByUserId(UUID userId, boolean active);
    void updateRefreshTokenStatus(RefreshToken refreshToken, String status, String requesterIp, String requesterDevice);
    RefreshToken findById(UUID refreshTokenId);
    boolean validateRefreshTokenOwnership(UUID refreshTokenId, UUID userId);
    void updateRefreshTokenAndAudit(UUID refreshTokenId, String actionType, String requesterIp, String requesterDevice);
}
