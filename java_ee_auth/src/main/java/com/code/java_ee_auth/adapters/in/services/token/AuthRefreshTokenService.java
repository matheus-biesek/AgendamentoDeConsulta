package com.code.java_ee_auth.adapters.in.services.token;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.code.java_ee_auth.adapters.in.security.RefreshTokenService;
import com.code.java_ee_auth.adapters.in.services.user.UserValidator;
import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenAuditDAO;
import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserRoleDAO;
import com.code.java_ee_auth.adapters.utils.CookieUtil;
import com.code.java_ee_auth.domain.dto.request.RefreshTokenUpdateDTO;
import com.code.java_ee_auth.domain.dto.response.LoginResponseDTO;
import com.code.java_ee_auth.domain.enuns.ActionType;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class AuthRefreshTokenService {
    
    private static final Logger logger = Logger.getLogger(AuthRefreshTokenService.class.getName());
    
    @Inject
    private RefreshTokenService refreshTokenService;
    @Inject
    private UserDAOImpl userDao;
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;
    @Inject
    private RefreshTokenAuditDAO refreshTokenAuditDAO;
    @Inject
    private AuthAccessTokenService authAccessTokenService;
    @Inject
    private UserRoleDAO userRoleDAO;

    public LoginResponseDTO validateRefreshTokenAtEndpoint(String tokenString, String requesterDevice, String requesterIp) {
        if (requesterDevice == null || requesterIp == null) {
            logger.warning("Usuário está tentando acessar o sistema sem informar o User-Agent ou o IP do sistema!");
            throw new RuntimeException("Token expirado!");
        }
        try {
            Claims claims = refreshTokenService.parseToken(tokenString);
            if (!claims.getIssuer().equals("auth-service")) {
                logger.warning("Token enviado não é do serviço de autenticação!");
                throw new RuntimeException("Token expirado!");
            }

            String refreshTokenId = claims.get("refreshTokenId").toString();
            RefreshToken refreshToken = refreshTokenDao.findById(UUID.fromString(refreshTokenId));

            String userId = claims.get("userId").toString();
            Optional<User> user = userDao.findById(UUID.fromString(userId));

            UserValidator.validateUser(user);
            
            validateRefreshToken(refreshToken, user.get(), requesterIp, requesterDevice);

            List<String> roles = userRoleDAO.getRolesByUserId(user.get().getId());
            if (roles.isEmpty()) {
                throw new RuntimeException("Usuário não possui funções atribuídas!");
            }

            Map<String, String> accessToken = authAccessTokenService.createWithCsrf(user.get().getId().toString(), roles);
            handleRefreshTokenAction(ActionType.REFRESH, refreshToken.getId(), requesterIp, requesterDevice);
            
            int maxAge = 60 * 60 * 6;
            String accessTokenCookie = CookieUtil.createCookie("accessToken", accessToken.get("accessToken"), "/", maxAge, true, true);
            String csrfTokenCookie = CookieUtil.createCookie("csrfToken", accessToken.get("csrfToken"), "/", maxAge, false, true);
            
            LoginResponseDTO response = new LoginResponseDTO(accessTokenCookie, csrfTokenCookie, null, roles);
            return response;

        } catch (ExpiredJwtException e) {
            String refreshTokenId = e.getClaims().get("refreshTokenId").toString();
            handleRefreshTokenAction(ActionType.EXPIRED, UUID.fromString(refreshTokenId), requesterIp, requesterDevice);
            throw new RuntimeException("Token expirado!");
        }
    }

    public void enforceTokenLimitPolicy(List<RefreshToken> activeTokens, String requesterIp, String requesterDevice) {
        if (activeTokens.size() >= 2) {
            if (activeTokens.size() > 2) {
                throw new RuntimeException("Contate o administrador do sistema, pois o usuário possui mais de 2 tokens de atualização ativos no sistema!");
            }
    
            RefreshToken oldestToken = activeTokens.stream()
                .min(Comparator.comparing(RefreshToken::getCreatedAt))
                .get();
    
            
            RefreshTokenUpdateDTO refreshTokenUpdateDTO = new RefreshTokenUpdateDTO(oldestToken.getId());
            refreshTokenUpdateDTO.setActive(false);
            refreshTokenDao.update(refreshTokenUpdateDTO);
            refreshTokenAuditDAO.create(oldestToken.getId(), requesterIp, requesterDevice, ActionType.REVOKED);
        }
    }

    public String createRefreshToken(RefreshToken refreshToken, UUID userId, String requesterIp, String requesterDevice) {
        Map<String, Object> claimsRefresh = new HashMap<>();
        claimsRefresh.put("refreshTokenId", refreshToken.getId());
        claimsRefresh.put("userId", userId);
        String refreshTokenString = refreshTokenService.generateToken(refreshToken.getId().toString(), claimsRefresh);
        
        refreshTokenDao.create(refreshToken);
        refreshTokenAuditDAO.create(refreshToken.getId(), requesterIp, requesterDevice, ActionType.CREATED);
        return refreshTokenString;
    }

    public void handleRefreshTokenAction(ActionType actionType, UUID refreshTokenId, String requesterIp, String requesterDevice) {
        RefreshTokenUpdateDTO refreshTokenUpdateDTO = new RefreshTokenUpdateDTO(refreshTokenId);

        if (!actionType.equals(ActionType.INACTIVE) && !actionType.equals(ActionType.REFRESH)) {
            refreshTokenUpdateDTO.setActive(false);
            refreshTokenDao.update(refreshTokenUpdateDTO);
        }
        refreshTokenAuditDAO.create(refreshTokenId, requesterIp, requesterDevice, actionType);
    }

    public void validateRefreshToken(RefreshToken refreshToken, User user, String requesterIp, String requesterDevice) {
        if (refreshToken == null) {
            throw new RuntimeException("Token de refresh não encontrado");
        }
        
        if (!refreshTokenDao.validateOwnership(refreshToken.getId(), user.getId())) {
            logger.warning("Token de refresh não pertence ao usuário");
            handleRefreshTokenAction(ActionType.TOKEN_BINDING_MISMATCH, refreshToken.getId(), requesterIp, requesterDevice);
            throw new RuntimeException("Token expirado!");
        }
        
        if (!requesterIp.equals(refreshToken.getUserIp()) || !requesterDevice.equals(refreshToken.getUserDevice())) {
            logger.warning("IP ou dispositivo não corresponde ao registrado");
            handleRefreshTokenAction(ActionType.TOKEN_BINDING_MISMATCH, refreshToken.getId(), requesterIp, requesterDevice);
            throw new RuntimeException("Token expirado!");
        }
        
        if (!refreshToken.isActive()) {
            logger.warning("Token de refresh não está ativo");
            handleRefreshTokenAction(ActionType.INACTIVE, refreshToken.getId(), requesterIp, requesterDevice);
            throw new RuntimeException("Token expirado!");
        }
    }
}
