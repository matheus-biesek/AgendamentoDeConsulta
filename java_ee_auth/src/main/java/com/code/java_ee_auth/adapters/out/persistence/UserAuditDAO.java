package com.code.java_ee_auth.adapters.out.persistence;

import com.code.java_ee_auth.domain.model.UserAudit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class UserAuditDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public void create(UserAudit userAudit) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO auth_service.user_audit (user_id, action_type, changed_by) VALUES (?, ?, ?)")
                    .setParameter(1, userAudit.getUserId())
                    .setParameter(2, userAudit.getActionType().getValue())
                    .setParameter(3, userAudit.getChangedBy())
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar registro de auditoria de usuário", e);
        }
    }
}
