package com.code.java_ee_auth.adapters.rest;

import java.util.Optional;
import java.util.UUID;
import com.code.java_ee_auth.adapters.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.application.service.security.AccessJWTService;
import com.code.java_ee_auth.application.service.security.RefreshJWTService;
import com.code.java_ee_auth.domain.dto.response.RefreshTokenResultDTO;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RefreshTokenService {
    
    @Inject
    private RefreshJWTService refreshJWTService;
    @Inject
    private UserDAOImpl userDao;
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;
    @Inject
    private AccessJWTService accessJWTService;
    
    public RefreshTokenResultDTO autheticationRefreshToken(String sendRefreshToken, String requesterDevice, String requesterIp) {
        if (sendRefreshToken == null || requesterDevice == null || requesterIp == null) {
            System.out.println("Há dados faltando para autenticação do refresh token!");
            return new RefreshTokenResultDTO(false, null, null, "Dados incompletos para autenticação");
        }
        String refreshTokenId = null;
        try {
            Claims claims = refreshJWTService.parseToken(sendRefreshToken);
            if ( !claims.getIssuer().equals("auth-service")) {
                System.out.println("Token de refresh nao condiz com o issuer!");
                return new RefreshTokenResultDTO(false, null, null, "Token de refresh inválido");
            }
            String userId = claims.getSubject();
            Optional<User> user = userDao.findById(UUID.fromString(userId));
            if (user == null) {
                System.out.println("Usuario nao encontrado!");
                return new RefreshTokenResultDTO(false, null, null, "Usuário não encontrado");
            }
            if (!user.get().isActive()) {
                System.out.println("Usuario nao esta ativo!");
                return new RefreshTokenResultDTO(false, null, null, "Usuário não está ativo");
            }
            if (user.get().isBlocked()) {
                System.out.println("Usuario bloqueado!");
                return new RefreshTokenResultDTO(false, null, null, "Usuário bloqueado");
            }
            System.out.println("usuario validado com sucesso");
            
            refreshTokenId = (String) claims.get("refreshTokenId");
            RefreshToken refreshToken = refreshTokenDao.findById(UUID.fromString(refreshTokenId));
            
            if (refreshToken == null) {
                System.out.println("Token de refresh nao encontrado!");
                return new RefreshTokenResultDTO(false, null, null, "Token de refresh não encontrado");
            }
            if ( !refreshTokenDao.validateRefreshTokenOwnership(UUID.fromString(refreshTokenId), user.get().getId())) {
                System.out.println("Token de refresh nao condiz com o id do usuario!");
                return new RefreshTokenResultDTO(false, null, null, "Token de refresh não pertence ao usuário");
            }
            if ( !requesterIp.equals(refreshToken.getUserIp()) || !requesterDevice.equals(refreshToken.getUserDevice())) {
                System.out.println("Ip ou dispositivo nao bate com o do banco de dados!");
                return new RefreshTokenResultDTO(false, null, null, "IP ou dispositivo não corresponde ao registrado");
            }
            if (refreshToken.isActive() == false) {
                System.out.println("Token de refresh nao esta ativo!");
                return new RefreshTokenResultDTO(false, null, null, "Token de refresh não está ativo");
            }
            System.out.println("Passou da validacao do refresh token");

            String csrfToken = UUID.randomUUID().toString();
            String accessToken = accessJWTService.generateToken(user.get().getId(), user.get().getRole().name(), csrfToken);
            
            refreshTokenDao.updateRefreshTokenAndAudit(
                UUID.fromString(refreshTokenId), 
                "REFRESH", 
                requesterIp, 
                requesterDevice
            );

            System.out.println("Token de refresh atualizado com sucesso no banco de dados!");
            return new RefreshTokenResultDTO(true, accessToken, csrfToken, null);

        } catch (ExpiredJwtException e) {
            // Se o token estiver expirado, atualizar o status para EXPIRED
            try {
                refreshTokenDao.updateRefreshTokenAndAudit(
                    UUID.fromString(refreshTokenId), 
                    "EXPIRED", 
                    requesterIp, 
                    requesterDevice
                );
            } catch (Exception ex) {
                System.out.println("Erro ao atualizar status do token expirado: " + ex.getMessage());
            }
            return new RefreshTokenResultDTO(false, null, null, "Token expirado");
        } catch (Exception e) {
            System.out.println("Erro ao processar refresh token: " + e.getMessage());
            return new RefreshTokenResultDTO(false, null, null, "Erro ao processar refresh token");
        }
    }
}
