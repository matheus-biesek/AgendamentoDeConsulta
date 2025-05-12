package com.code.java_ee_auth.adapters.out.persistence;

import java.util.List;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRoleDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void addRoleToUser(UUID userId, String roleName) {
        try {
            entityManager.createNativeQuery("INSERT INTO auth_service.user_roles (user_id, role_name) VALUES (?, ?)")
                .setParameter(1, userId)
                .setParameter(2, roleName)
                .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao adicionar função ao usuário!", e);
        }
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, String roleName) {
        try {
            entityManager.createNativeQuery("DELETE FROM auth_service.user_roles WHERE user_id = ? AND role_name = ?")
                .setParameter(1, userId)
                .setParameter(2, roleName)
                .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover função do usuário!", e);
        }
    }

    public List<String> getRolesByUserId(UUID userId) {
        try {
            return entityManager.createNativeQuery("SELECT role_name FROM auth_service.user_roles WHERE user_id = ?")
                .setParameter(1, userId)
                .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter funções do usuário!", e);
        }
    }

    public List<UUID> getUsersByRole(String roleName) {
        try {
            return entityManager.createNativeQuery("SELECT user_id FROM auth_service.user_roles WHERE role_name = ?")
                .setParameter(1, roleName)
                .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter usuários por função!", e);
        }
    }
}
