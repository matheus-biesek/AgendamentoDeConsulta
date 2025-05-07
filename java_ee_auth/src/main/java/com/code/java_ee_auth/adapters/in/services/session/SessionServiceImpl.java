package com.code.java_ee_auth.adapters.in.services.session;

import com.code.java_ee_auth.adapters.in.services.security.AccessTokenService;
import com.code.java_ee_auth.adapters.in.services.security.AuthUserService;
import com.code.java_ee_auth.adapters.in.services.security.PasswordService;
import com.code.java_ee_auth.adapters.in.services.security.RefreshTokenService;
import com.code.java_ee_auth.adapters.in.services.user.UserValidator;
import com.code.java_ee_auth.adapters.out.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import com.code.java_ee_auth.domain.dto.response.RoleDTO;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.in.SessionServicePort;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionServiceImpl implements SessionServicePort {

    @Inject
    private UserDAOImpl userRepository;
    
    @Inject
    private AccessTokenService accessTokenService;
    
    @Inject
    private RefreshTokenService refreshTokenService;
    
    @Inject
    private PasswordService passwordService;
    
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;

    @Inject
    private AuthUserService authUserService;

    @Override
    public Response login(LoginDTO credentials, String requesterDevice, String requesterIp) {
        if (requesterDevice == null || requesterIp == null) {
            System.out.println("Usuário está tentando acessar o sistema sem informar o User-Agent ou o IP do sistema!");
            System.out.println("User-Agent: " + requesterDevice);
            System.out.println("X-Forwarded-For: " + requesterIp);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        Optional<User> userOpt = userRepository.findByCpf(credentials.getCpf());

        if (!UserValidator.validateUser(userOpt)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Usuario nao validado")
                    .build();
        }

        if (!passwordService.verify(credentials.getPassword(), userOpt.get().getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        List<RefreshToken> refreshTokens = refreshTokenDao.findRefreshTokensByUserId(userOpt.get().getId(), true);
        
        if (refreshTokens.isEmpty()) {
            System.out.println("Nenhum refresh token ativo encontrado para o usuário");
        }

        if (refreshTokens.size() >= 2) {
            if (refreshTokens.size() > 2) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Contate o administrador do sistema, pois o usuário possui mais de 2 tokens de atualização ativos no sistema!")
                    .build();
            }

            if (refreshTokens.size() == 2) {
                System.out.println("2 tokens ativos");
            }

            RefreshToken oldestToken = refreshTokens.stream()
                .min((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .get();
            
            oldestToken.setActive(false);
            refreshTokenDao.updateRefreshTokenStatus(oldestToken, "REVOKED", requesterIp, requesterDevice);
        }

        RefreshToken refreshTokenEntity = new RefreshToken(
            UUID.randomUUID(),
            userOpt.get().getId(), 
            requesterIp,  
            requesterDevice, 
            LocalDateTime.now().plusHours(7));
            
        String refreshToken = refreshTokenService.generateToken(refreshTokenEntity.getUserId(), refreshTokenEntity.getId());
        refreshTokenDao.saveRefreshToken(refreshTokenEntity);

        String csrfToken = UUID.randomUUID().toString();
        String token = accessTokenService.generateToken(userOpt.get().getId(), csrfToken, userOpt.get().getRole());
       
        String jwtCookie = String.format(
            "accessToken=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict",
            token,
            60 * 60 * 6
        );

        String csrfCookie = String.format(
            "csrfToken=%s; Path=/; Max-Age=%d; SameSite=Strict",
            csrfToken,
            60 * 60 * 6
        );

        String refreshTokenCookie = String.format(
            "refreshToken=%s; Path=/rest-auth/token/refresh; Max-Age=%d; HttpOnly; SameSite=Strict",
            refreshToken,
            60 * 60 * 6
        );

        RoleDTO dto = new RoleDTO(userOpt.get().getRole());
        return Response.ok(dto)
                .header("Set-Cookie", jwtCookie)
                .header("Set-Cookie", csrfCookie)
                .header("Set-Cookie", refreshTokenCookie)
                .build();
    }

    @Override
    public Response logout(String tokenString, String requesterDevice, String requesterIp) {
        String expiredCookieToken = "accessToken=; Path=/; Max-Age=0; HttpOnly; SameSite=Strict";
        String expiredCookieCsrfToken = "csrfToken=; Path=/; Max-Age=0; SameSite=Strict";
        String expiredCookieRefreshToken = "refreshToken=; Path=/rest-auth; Max-Age=0; HttpOnly; SameSite=Strict";
        
        if (tokenString == null) {
            return Response.status(Response.Status.OK).build();
        }
        
        try {
            Claims claims = accessTokenService.parseToken(tokenString);
            UUID userId = UUID.fromString(claims.getSubject());
            
            authUserService.revokeAllUserTokens(userId, requesterIp, requesterDevice);

        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado");
        }

        return Response.ok()
            .header("Set-Cookie", expiredCookieToken)
            .header("Set-Cookie", expiredCookieCsrfToken)
            .header("Set-Cookie", expiredCookieRefreshToken)
            .build();
    }
} 