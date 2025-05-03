package com.code.java_ee_auth.domain.port.in;

import java.util.UUID;
import com.code.java_ee_auth.domain.dto.response.RefreshTokenResultDTO;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import io.jsonwebtoken.Claims;

public interface AuthRefreshTokenServicePort {
    RefreshTokenResultDTO autheticationRefreshToken(String sendRefreshToken, String requesterDevice, String requesterIp);
    boolean validateInputParameters(String sendRefreshToken, String requesterDevice, String requesterIp);
    boolean validateIssuer(Claims claims);
    boolean validateRefreshToken(RefreshToken refreshToken, User user, String refreshTokenId, String requesterIp, String requesterDevice);
    void handleTokenBindingMismatch(String refreshTokenId, UUID userId, String requesterIp, String requesterDevice);
    void handleInactiveToken(String refreshTokenId, UUID userId, String requesterIp, String requesterDevice);
    void handleExpiredToken(String refreshTokenId, String requesterIp, String requesterDevice);
}
