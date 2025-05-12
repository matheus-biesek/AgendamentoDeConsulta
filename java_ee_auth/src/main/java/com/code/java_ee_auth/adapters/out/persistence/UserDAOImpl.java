package com.code.java_ee_auth.adapters.out.persistence;

import com.code.java_ee_auth.domain.dto.request.UserUpdateDTO;
import com.code.java_ee_auth.domain.enuns.Gender;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.out.UserDAOPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserDAOImpl implements UserDAOPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void create(User userSend) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO auth_service.users (user_id, name, cpf, email, password, gender) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, userSend.getId())
                    .setParameter(2, userSend.getName())
                    .setParameter(3, userSend.getCpf())
                    .setParameter(4, userSend.getEmail())
                    .setParameter(5, userSend.getPassword())
                    .setParameter(6, userSend.getGender().name())
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao salvar usuário!", e);
        }
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT user_id, active, name, cpf, email, password, gender, blocked FROM auth_service.users WHERE cpf = :cpf")
                .setParameter("cpf", cpf)
                .getResultList();
            
            if (results.isEmpty()) {
                return Optional.empty();
            }
            
            Object[] row = results.get(0);
            User user = new User();
            
            user.setId((UUID) row[0]);
            user.setActive((Boolean) row[1]);  
            user.setName((String) row[2]);  
            user.setCpf((String) row[3]);  
            user.setEmail((String) row[4]);  
            user.setPassword((String) row[5]);
            user.setGender(Gender.valueOf((String) row[6]));  
            user.setBlocked((Boolean) row[7]);  
            
            return Optional.of(user);

        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao buscar usuário no banco de dados", e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT user_id, active, name, cpf, email, password, gender, blocked FROM auth_service.users WHERE user_id = :id")
                .setParameter("id", id)
                .getResultList();
            
            if (results.isEmpty()) {
                return Optional.empty();
            }
            
            Object[] row = results.get(0);
            User user = new User();
            user.setId((UUID) row[0]); 
            user.setActive((Boolean) row[1]);  
            user.setName((String) row[2]);  
            user.setCpf((String) row[3]);  
            user.setEmail((String) row[4]);  
            user.setPassword((String) row[5]);  
            user.setGender(Gender.valueOf((String) row[6]));  
            user.setBlocked((Boolean) row[7]);  
            
            System.out.println("Usuário encontrado por ID - ID: " + user.getId() + ", Blocked: " + user.isBlocked() + ", Active: " + user.isActive());
            
            return Optional.of(user);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao buscar usuário por ID!", e);
        }
    }

    @Override
    @Transactional
    public void updateUserByCpf(UserUpdateDTO dto) {
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new RuntimeException("CPF é obrigatório para atualização");
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE auth_service.users SET ");
        Map<String, Object> params = new HashMap<>();

        if (dto.getName() != null) {
            queryBuilder.append("name = :name, ");
            params.put("name", dto.getName());
        }

        if (dto.getEmail() != null) {
            queryBuilder.append("email = :email, ");
            params.put("email", dto.getEmail());
        }

        if (dto.getGender() != null) {
            queryBuilder.append("gender = :gender, ");
            params.put("gender", dto.getGender().name());
        }

        if (dto.getActive() != null) {
            queryBuilder.append("active = :active, ");
            params.put("active", dto.getActive());
        }

        if (dto.getBlocked() != null) {
            queryBuilder.append("blocked = :blocked, ");
            params.put("blocked", dto.getBlocked());
        }

        if (params.isEmpty()) {
            throw new RuntimeException("Nenhum campo para atualizar foi fornecido");
        }

        // Remove a última vírgula e espaço
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE cpf = :cpf");
        params.put("cpf", dto.getCpf());

        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            int updatedRows = query.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("Usuário não encontrado para atualização!");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao atualizar usuário!", e);
        }
    }

}
