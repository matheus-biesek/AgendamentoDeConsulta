package com.code.java_ee_auth.adapters.in.services.session;

import java.util.Optional;
import java.util.UUID;

import com.code.java_ee_auth.adapters.in.services.security.AccessTokenService;
import com.code.java_ee_auth.adapters.in.services.security.AuthUserService;
import com.code.java_ee_auth.adapters.in.services.security.RefreshTokenService;
import com.code.java_ee_auth.adapters.in.services.user.UserValidator;
import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.domain.dto.response.RefreshTokenResultDTO;
import com.code.java_ee_auth.domain.enuns.AuthError;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.in.AuthRefreshTokenServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthRefreshTokenService implements AuthRefreshTokenServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthRefreshTokenService.class);
    
    private static final String AUTH_SERVICE_ISSUER = "auth-service";
    
    @Inject
    private RefreshTokenService refreshTokenService;
    @Inject
    private UserDAOImpl userDao;
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;
    @Inject
    private AccessTokenService accessTokenService;
    @Inject
    private AuthUserService authUserService;

    @Override
    public RefreshTokenResultDTO autheticationRefreshToken(String sendRefreshToken, String requesterDevice, String requesterIp) {
        if (!validateInputParameters(sendRefreshToken, requesterDevice, requesterIp)) {
            return new RefreshTokenResultDTO(false, null, null, AuthError.TOKEN_MISSING.getMessage());
        }
        
        String refreshTokenId = null;
        try {
            Claims claims = refreshTokenService.parseToken(sendRefreshToken);
            if (!validateIssuer(claims)) {
                return new RefreshTokenResultDTO(false, null, null, AuthError.TOKEN_INVALID.getMessage());
            }
            
            String userId = claims.getSubject();
            Optional<User> user = userDao.findById(UUID.fromString(userId));
            
            if (!UserValidator.validateUser(user)) {
                return new RefreshTokenResultDTO(false, null, null, AuthError.USER_NOT_FOUND.getMessage());
            }
            
            logger.info("Usuário validado com sucesso");
            
            refreshTokenId = (String) claims.get("refreshTokenId");
            RefreshToken refreshToken = refreshTokenDao.findById(UUID.fromString(refreshTokenId));
            
            if (!validateRefreshToken(refreshToken, user.get(), refreshTokenId, requesterIp, requesterDevice)) {
                return new RefreshTokenResultDTO(false, null, null, AuthError.TOKEN_INVALID.getMessage());
            }
            
            logger.info("Token de refresh validado com sucesso");

            String csrfToken = UUID.randomUUID().toString();
            String accessToken = accessTokenService.generateToken(user.get().getId(), csrfToken, user.get().getRole());
            
            refreshTokenDao.updateRefreshTokenAndAudit(
                UUID.fromString(refreshTokenId), 
                "REFRESH", 
                requesterIp, 
                requesterDevice
            );

            logger.info("Token de refresh atualizado com sucesso no banco de dados");
            return new RefreshTokenResultDTO(true, accessToken, csrfToken, null);

        } catch (ExpiredJwtException e) {
            refreshTokenId = e.getClaims().get("refreshTokenId").toString();
            handleExpiredToken(refreshTokenId, requesterIp, requesterDevice);
            return new RefreshTokenResultDTO(false, null, null, AuthError.TOKEN_EXPIRED.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao processar refresh token: {}", e.getMessage());
            return new RefreshTokenResultDTO(false, null, null, AuthError.TOKEN_INVALID.getMessage());
        }
    }

    @Override
    public boolean validateInputParameters(String sendRefreshToken, String requesterDevice, String requesterIp) {
        if (sendRefreshToken == null || requesterDevice == null || requesterIp == null) {
            logger.warn("Dados incompletos para autenticação do refresh token");
            return false;
        }
        return true;
    }

    @Override
    public boolean validateIssuer(Claims claims) {
        return AUTH_SERVICE_ISSUER.equals(claims.getIssuer());
    }

    @Override
    public boolean validateRefreshToken(RefreshToken refreshToken, User user, String refreshTokenId, String requesterIp, String requesterDevice) {
        if (refreshToken == null) {
            logger.warn("Token de refresh não encontrado");
            return false;
        }
        
        if (!refreshTokenDao.validateRefreshTokenOwnership(UUID.fromString(refreshTokenId), user.getId())) {
            logger.warn("Token de refresh não pertence ao usuário");
            return false;
        }
        
        if (!requesterIp.equals(refreshToken.getUserIp()) || !requesterDevice.equals(refreshToken.getUserDevice())) {
            logger.warn("IP ou dispositivo não corresponde ao registrado");
            handleTokenBindingMismatch(refreshTokenId, user.getId(), requesterIp, requesterDevice);
            return false;
        }
        
        if (!refreshToken.isActive()) {
            logger.warn("Token de refresh não está ativo");
            handleInactiveToken(refreshTokenId, user.getId(), requesterIp, requesterDevice);
            return false;
        }
        
        return true;
    }

    @Override
    public void handleTokenBindingMismatch(String refreshTokenId, UUID userId, String requesterIp, String requesterDevice) {
        refreshTokenDao.updateRefreshTokenAndAudit(
            UUID.fromString(refreshTokenId), 
            "TOKEN_BINDING_MISMATCH", 
            requesterIp, 
            requesterDevice
        );
        authUserService.revokeAllUserTokens(userId, requesterIp, requesterDevice);
    }

    @Override
    public void handleInactiveToken(String refreshTokenId, UUID userId, String requesterIp, String requesterDevice) {
        refreshTokenDao.updateRefreshTokenAndAudit(
            UUID.fromString(refreshTokenId), 
            "INACTIVE", 
            requesterIp, 
            requesterDevice
        );
        authUserService.revokeAllUserTokens(userId, requesterIp, requesterDevice);
    }

    @Override
    public void handleExpiredToken(String refreshTokenId, String requesterIp, String requesterDevice) {
        try {
            refreshTokenDao.updateRefreshTokenAndAudit(
                UUID.fromString(refreshTokenId), 
                "EXPIRED", 
                requesterIp, 
                requesterDevice
            );
        } catch (Exception ex) {
            logger.error("Erro ao atualizar status do token expirado: {}", ex.getMessage());
        }
    }
}
