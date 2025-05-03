package com.code.java_ee_auth.adapters.in.services.security;

import java.util.List;
import java.util.UUID;

import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.port.in.AuthUserServicePort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthUserService implements AuthUserServicePort {
    
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;

    private static final Logger logger = LoggerFactory.getLogger(AuthUserService.class);

    /**.
     * @param userId           ID do usuário
     * @param requesterIp      IP de quem está fazendo a requisição
     * @param requesterDevice  Dispositivo que solicitou a revogação
    */
    @Override
    public void revokeAllUserTokens(UUID userId, String requesterIp, String requesterDevice) {
        List<RefreshToken> activeTokens = refreshTokenDao.findRefreshTokensByUserId(userId, true);

        for (RefreshToken token : activeTokens) {
            token.setActive(false);
            refreshTokenDao.updateRefreshTokenStatus(token, "REVOKED", requesterIp, requesterDevice);
        }
    }
}
