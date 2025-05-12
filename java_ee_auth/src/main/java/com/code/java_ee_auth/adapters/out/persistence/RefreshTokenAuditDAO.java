package com.code.java_ee_auth.adapters.out.persistence;

import java.util.UUID;

import com.code.java_ee_auth.domain.enuns.ActionType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
@ApplicationScoped
public class RefreshTokenAuditDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void create(UUID refreshTokenId, String requesterIp, String requesterDevice, ActionType actionType) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO auth_service.refresh_token_audit (refresh_token_id, requester_ip_address, requester_device_name, action_type) VALUES (?, ?, ?, ?)")
                    .setParameter(1, refreshTokenId)
                    .setParameter(2, requesterIp)
                    .setParameter(3, requesterDevice)
                    .setParameter(4, actionType.toString())
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar registro de auditoria de token de atualização", e);
        }
    }
}