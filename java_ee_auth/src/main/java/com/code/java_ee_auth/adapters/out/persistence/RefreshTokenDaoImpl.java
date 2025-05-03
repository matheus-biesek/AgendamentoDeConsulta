package com.code.java_ee_auth.adapters.out.persistence;

import java.util.UUID;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.port.out.RefreshTokenDaoPort;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RefreshTokenDaoImpl implements RefreshTokenDaoPort {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean saveRefreshToken(RefreshToken refreshToken) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT auth_service.create_refresh_token(?, ?, ?, ?, ?)")
                    .setParameter(1, refreshToken.getId())
                    .setParameter(2, refreshToken.getUserId())
                    .setParameter(3, refreshToken.getUserIp())
                    .setParameter(4, refreshToken.getUserDevice())
                    .setParameter(5, refreshToken.getExpiryDate())
                    .getSingleResult();

            // O PostgreSQL retorna um Boolean como um objeto
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else if (result instanceof Number) {
                // Alguns drivers podem retornar 1/0 como Number
                return ((Number) result).intValue() == 1;
            }
            
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar token de atualização: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<RefreshToken> findRefreshTokensByUserId(UUID userId, boolean active) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT refresh_token_id, active, created_at " +
                "FROM auth_service.refresh_tokens " + 
                "WHERE user_id = ? AND active = ?")
                .setParameter(1, userId)
                .setParameter(2, active)
                .getResultList();

            List<RefreshToken> tokens = new ArrayList<>();
            
            for (Object[] result : results) {
                RefreshToken token = new RefreshToken();
                token.setId((UUID) result[0]);
                token.setActive((boolean) result[1]);
                
                // Converter Timestamp para LocalDateTime
                if (result[2] instanceof java.sql.Timestamp) {
                    java.sql.Timestamp timestamp = (java.sql.Timestamp) result[2];
                    token.setCreatedAt(timestamp.toLocalDateTime());
                } else {
                    token.setCreatedAt((LocalDateTime) result[2]);
                }
                
                tokens.add(token);
            }

            return tokens;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tokens de atualização do usuário: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateRefreshTokenStatus(RefreshToken refreshToken, String status, String requesterIp, String requesterDevice) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT auth_service.update_refresh_token_status(?, ?, ?, ?, ?)")
                    .setParameter(1, refreshToken.isActive())
                    .setParameter(2, status)
                    .setParameter(3, refreshToken.getId())
                    .setParameter(4, requesterIp)
                    .setParameter(5, requesterDevice)
                    .getSingleResult();
                
            if (result instanceof Boolean) {
                boolean success = (Boolean) result;
                if (!success) {
                    throw new RuntimeException("Falha ao atualizar status do token de atualização");
                }
            } else if (result instanceof Number) {
                int success = ((Number) result).intValue();
                if (success != 1) {
                    throw new RuntimeException("Falha ao atualizar status do token de atualização");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status do token de atualização: " + e.getMessage(), e);
        }
    }

    @Override
    public RefreshToken findById(UUID refreshTokenId) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT refresh_token_id, active, user_ip_address, user_device_name " +
                "FROM auth_service.refresh_tokens " +
                "WHERE refresh_token_id = ?")
                    .setParameter(1, refreshTokenId)
                    .getSingleResult();
                
            if (result instanceof Object[]) {
                Object[] resultArray = (Object[]) result;
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setId((UUID) resultArray[0]);
                refreshToken.setActive((Boolean) resultArray[1]);
                refreshToken.setUserIp((String) resultArray[2]);
                refreshToken.setUserDevice((String) resultArray[3]);
                return refreshToken;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar token de atualização: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateRefreshTokenOwnership(UUID refreshTokenId, UUID userId) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT EXISTS (" +
                "  SELECT 1 FROM auth_service.refresh_tokens " + 
                "  WHERE refresh_token_id = ? AND user_id = ?" +
                ")")
                .setParameter(1, refreshTokenId)
                .setParameter(2, userId)
                .getSingleResult();

            if (result instanceof Boolean) {
                return (Boolean) result;
            } else if (result instanceof Number) {
                return ((Number) result).intValue() == 1;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar propriedade do token de atualização: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void updateRefreshTokenAndAudit(UUID refreshTokenId, String actionType, String requesterIp, String requesterDevice) {
        try {
            // Chamar a função PL/pgSQL que atualiza o refresh token e registra no audit
            Object result = entityManager.createNativeQuery(
                "SELECT auth_service.update_refresh_token_for_api_refresh(?, ?, ?, ?)")
                .setParameter(1, refreshTokenId)
                .setParameter(2, actionType)
                .setParameter(3, requesterIp)
                .setParameter(4, requesterDevice)
                .getSingleResult();
                
            // Verificar se o resultado é um booleano
            if (result instanceof Boolean) {
                boolean success = (Boolean) result;
                if (!success) {
                    throw new RuntimeException("Falha ao atualizar refresh token e registrar audit");
                }
            } else if (result instanceof Number) {
                // Alguns drivers podem retornar 1/0 como Number
                int success = ((Number) result).intValue();
                if (success != 1) {
                    throw new RuntimeException("Falha ao atualizar refresh token e registrar audit");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar refresh token e registrar audit: " + e.getMessage(), e);
        }
    }
}
