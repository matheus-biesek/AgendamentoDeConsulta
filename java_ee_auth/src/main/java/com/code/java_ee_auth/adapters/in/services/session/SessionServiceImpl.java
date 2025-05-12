package com.code.java_ee_auth.adapters.in.services.session;

import com.code.java_ee_auth.adapters.in.security.RefreshTokenService;
import com.code.java_ee_auth.adapters.in.services.token.AuthAccessTokenService;
import com.code.java_ee_auth.adapters.in.services.token.AuthRefreshTokenService;
import com.code.java_ee_auth.adapters.in.services.user.UserValidator;
import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserRoleDAO;
import com.code.java_ee_auth.adapters.utils.CookieUtil;
import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import com.code.java_ee_auth.domain.dto.response.LoginResponseDTO;
import com.code.java_ee_auth.domain.enuns.ActionType;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.in.SessionServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionServiceImpl implements SessionServicePort {

    Logger logger = Logger.getLogger(SessionServiceImpl.class.getName());

    @Inject
    private UserDAOImpl userRepository;
    
    @Inject
    private RefreshTokenService refreshTokenService;
    @Inject
    private PasswordService passwordService;
    
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;

    @Inject
    private UserRoleDAO userRoleDAO;

    @Inject
    private AuthRefreshTokenService authRefreshTokenService;

    @Inject
    private AuthAccessTokenService authAccessTokenService;

    @Override
    public LoginResponseDTO login(LoginDTO credentials, String requesterDevice, String requesterIp) {
        if (requesterDevice == null || requesterIp == null) {
            logger.warning("Usuário está tentando acessar o sistema sem informar o User-Agent ou o IP do sistema!");
            throw new RuntimeException("Credenciais inválidas!");
        }

        Optional<User> userOpt = userRepository.findByCpf(credentials.getCpf());
        
        UserValidator.validateUser(userOpt);

        if (!passwordService.verify(credentials.getPassword(), userOpt.get().getPassword())) {
            throw new RuntimeException("Credenciais inválidas!");
        }

        List<RefreshToken> refreshTokens = refreshTokenDao.findAllByUserId(userOpt.get().getId(), true);

        authRefreshTokenService.enforceTokenLimitPolicy(refreshTokens, requesterIp, requesterDevice);

        RefreshToken refreshTokenEntity = new RefreshToken(
            UUID.randomUUID(),
            userOpt.get().getId(), 
            requesterIp,  
            requesterDevice, 
            LocalDateTime.now().plusHours(7));

        String refreshToken = authRefreshTokenService.createRefreshToken(refreshTokenEntity, userOpt.get().getId(), requesterIp, requesterDevice);

        List<String> roles = userRoleDAO.getRolesByUserId(userOpt.get().getId());
        if (roles.isEmpty()) {
            throw new RuntimeException("Usuário não possui funções atribuídas!");
        }

        Map<String, String> accessToken = authAccessTokenService.createWithCsrf(userOpt.get().getId().toString(), roles);
        int maxAge = 60 * 60 * 6;

        String accessTokenCookie = CookieUtil.createCookie("accessToken", accessToken.get("accessToken"), "/", maxAge, true, true);
        String csrfTokenCookie = CookieUtil.createCookie("csrfToken", accessToken.get("csrfToken"), "/", maxAge, false, true);
        String refreshTokenCookie = CookieUtil.createCookie("refreshToken", refreshToken, "/rest-auth/token/refresh", maxAge, true, true);
        
        LoginResponseDTO response = new LoginResponseDTO(accessTokenCookie, csrfTokenCookie, refreshTokenCookie, roles);
        return response;
    }

    @Override
    public LoginResponseDTO logout(String tokenString, String requesterDevice, String requesterIp) {

        String accessToken = CookieUtil.createCookie("accessToken", null, "/", 0, true, true);
        String csrfToken = CookieUtil.createCookie("csrfToken", null, "/", 0, false, true);
        String refreshToken = CookieUtil.createCookie("refreshToken", null, "/rest-auth/token/refresh", 0, true, true);
        
        if (tokenString == null) {
            return new LoginResponseDTO(accessToken, csrfToken, refreshToken, null);
        }
        
        try {
            Claims claims = refreshTokenService.parseToken(tokenString);
            UUID refreshTokenId = UUID.fromString(claims.getSubject());
            authRefreshTokenService.handleRefreshTokenAction(ActionType.REVOKED, refreshTokenId, requesterIp, requesterDevice);
            return new LoginResponseDTO(accessToken, csrfToken, refreshToken, null);
        } catch (ExpiredJwtException e) {
            Claims claims = refreshTokenService.parseToken(tokenString);
            UUID refreshTokenId = UUID.fromString(claims.getSubject());
            authRefreshTokenService.handleRefreshTokenAction(ActionType.REVOKED, refreshTokenId, requesterIp, requesterDevice);
            return new LoginResponseDTO(accessToken, csrfToken, refreshToken, null);
        }
    }
} 