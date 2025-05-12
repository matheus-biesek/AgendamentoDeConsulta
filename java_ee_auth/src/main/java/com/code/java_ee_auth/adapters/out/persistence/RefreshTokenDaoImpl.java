package com.code.java_ee_auth.adapters.out.persistence;

import java.util.UUID;

import com.code.java_ee_auth.domain.dto.request.RefreshTokenUpdateDTO;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.port.out.RefreshTokenDaoPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RefreshTokenDaoImpl implements RefreshTokenDaoPort {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean create(RefreshToken refreshToken) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO auth_service.refresh_tokens (refresh_token_id, user_id, user_ip_address, user_device_name, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, refreshToken.getId())
                .setParameter(2, refreshToken.getUserId())
                .setParameter(3, refreshToken.getUserIp())
                .setParameter(4, refreshToken.getUserDevice())
                .setParameter(5, refreshToken.getExpiryDate())
                .executeUpdate();

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar token de atualização", e);
        }
    }

    @Override
    @Transactional
    public void update(RefreshTokenUpdateDTO dto) {
        if (dto.getRefreshTokenId() == null) {
            throw new IllegalArgumentException("ID do refresh token é obrigatório para atualização");
        }
    
        StringBuilder queryBuilder = new StringBuilder("UPDATE auth_service.refresh_tokens SET ");
        Map<String, Object> params = new HashMap<>();

        if (dto.getActive() != null) {
            queryBuilder.append("active = :active, ");
            params.put("active", dto.getActive());
        }
    
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Nenhum campo fornecido para atualização");
        }
    
        // Remove a vírgula final
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE refresh_token_id = :id");
        params.put("id", dto.getRefreshTokenId());
    
        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            params.forEach(query::setParameter);
    
            int updatedRows = query.executeUpdate();
    
            if (updatedRows == 0) {
                throw new RuntimeException("Refresh token não encontrado");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar o status do refresh token", e);
        }
    }

    @Override
    public boolean validateOwnership(UUID refreshTokenId, UUID userId) {
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
            throw new RuntimeException("Erro ao validar propriedade do token de atualização", e);
        }
    }

    @Override
    public RefreshToken findById(UUID refreshTokenId) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT refresh_token_id, user_id, active, user_ip_address, user_device_name " +
                "FROM auth_service.refresh_tokens " +
                "WHERE refresh_token_id = ?")
                    .setParameter(1, refreshTokenId)
                    .getSingleResult();
                
            if (result instanceof Object[]) {
                Object[] resultArray = (Object[]) result;
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setId((UUID) resultArray[0]);
                refreshToken.setUserId((UUID) resultArray[1]);
                refreshToken.setActive((Boolean) resultArray[2]);
                refreshToken.setUserIp((String) resultArray[3]);
                refreshToken.setUserDevice((String) resultArray[4]);
                return refreshToken;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar token de atualização", e);
        }
    }

    @Override
    @Transactional
    public List<RefreshToken> findAllByUserId(UUID userId, boolean active) {
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
            throw new RuntimeException("Erro ao buscar tokens de atualização do usuário", e);
        }
    }
}
