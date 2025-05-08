package com.code.java_ee_auth.adapters.out.persistence;

import com.code.java_ee_auth.adapters.in.rest.exeception.UserDAOException;
import com.code.java_ee_auth.adapters.in.rest.exeception.UserNotFoundException;
import com.code.java_ee_auth.domain.enuns.Gender;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.out.UserDAOPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println("Username recebido: " + userSend.getName());
            System.out.println("Senha (hash): " + userSend.getPassword());
            System.out.println("Role: " + userSend.getRole().name());
            System.out.println("Gender: " + userSend.getGender().name());

            entityManager.createNativeQuery(
                "INSERT INTO auth_service.users (name, cpf, email, password, role, gender) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, userSend.getName())
                    .setParameter(2, userSend.getCpf())
                    .setParameter(3, userSend.getEmail())
                    .setParameter(4, userSend.getPassword())
                    .setParameter(5, userSend.getRole().name())
                    .setParameter(6, userSend.getGender().name())
                    .executeUpdate();
                    
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistence ao salvar usuário no banco!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao salvar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }


    @Override
    public Optional<User> findByCpf(String cpf) {
        try {
            System.out.println("CPF recebido: " + cpf);
            
            // Execute a query e obtenha o resultado como um array de objetos
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT user_id, active, name, cpf, email, password, role, gender, blocked FROM auth_service.users WHERE cpf = :cpf")
                .setParameter("cpf", cpf)
                .getResultList();
            
            // Se não houver resultados, retorne um Optional vazio
            if (results.isEmpty()) {
                return Optional.empty();
            }
            
            // Mapeie o resultado para um objeto User
            Object[] row = results.get(0);
            User user = new User();
            
            // Ajuste os índices e tipos de acordo com a ordem das colunas na sua tabela
            user.setId((UUID) row[0]);  // user_id
            user.setActive((Boolean) row[1]);  // active
            user.setName((String) row[2]);  // name
            user.setCpf((String) row[3]);  // cpf
            user.setEmail((String) row[4]);  // email
            user.setPassword((String) row[5]);  // password
            user.setRole(UserRole.valueOf((String) row[6]));  // role
            user.setGender(Gender.valueOf((String) row[7]));  // gender
            user.setBlocked((Boolean) row[8]);  // blocked
            
            System.out.println("Usuário encontrado - ID: " + user.getId() + ", Blocked: " + user.isBlocked() + ", Active: " + user.isActive());
            
            return Optional.of(user);
        } catch (PersistenceException e) {
            System.out.println("Erro de persistência ao buscar usuário por CPF: " + e);
            throw new UserDAOException("Erro de persistência ao buscar usuário no banco de dados",
                Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            System.out.println("Erro inesperado ao buscar usuário: " + e);
            throw new UserDAOException("Erro inesperado ao buscar usuário no banco de dados",
                Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void updateForSecretary(User user) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                "UPDATE auth_service.users SET name = :name, cpf = :cpf, email = :email, gender = :gender WHERE user_id = :id")
                    .setParameter("id", user.getId())
                    .setParameter("name", user.getName())
                    .setParameter("cpf", user.getCpf())
                    .setParameter("email", user.getEmail())
                    .setParameter("gender", user.getGender().name())
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new UserNotFoundException("Usuário não encontrado para atualização!");
            }
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistência ao atualizar dados do usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao atualizar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void updateForAdmin(User user) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                "UPDATE auth_service.users SET role = :role WHERE cpf = :cpf")
                    .setParameter("cpf", user.getCpf())
                    .setParameter("role", user.getRole().name())
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new UserNotFoundException("Usuário não encontrado para atualização!");
            }
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistência ao atualizar dados do usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao atualizar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void delete(String cpf) {
        try {
            int deletedRows = entityManager.createNativeQuery(
                "UPDATE auth_service.users SET active = false WHERE cpf = :cpf")
                    .setParameter("cpf", cpf)
                    .executeUpdate();

            if (deletedRows == 0) {
                throw new UserNotFoundException("Usuário não encontrado!");
            }
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro ao excluir usuário! Pode haver dependências no banco.", Response.Status.CONFLICT, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao deletar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            // Execute a query e obtenha o resultado como um array de objetos
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT user_id, active, name, cpf, email, password, role, gender, blocked FROM auth_service.users WHERE user_id = :id")
                .setParameter("id", id)
                .getResultList();
            
            // Se não houver resultados, retorne um Optional vazio
            if (results.isEmpty()) {
                return Optional.empty();
            }
            
            // Mapeie o resultado para um objeto User
            Object[] row = results.get(0);
            User user = new User();
            
            // Ajuste os índices e tipos de acordo com a ordem das colunas na sua tabela
            user.setId((UUID) row[0]);  // user_id
            user.setActive((Boolean) row[1]);  // active
            user.setName((String) row[2]);  // name
            user.setCpf((String) row[3]);  // cpf
            user.setEmail((String) row[4]);  // email
            user.setPassword((String) row[5]);  // password
            user.setRole(UserRole.valueOf((String) row[6]));  // role
            user.setGender(Gender.valueOf((String) row[7]));  // gender
            user.setBlocked((Boolean) row[8]);  // blocked
            
            System.out.println("Usuário encontrado por ID - ID: " + user.getId() + ", Blocked: " + user.isBlocked() + ", Active: " + user.isActive());
            
            return Optional.of(user);
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro ao buscar usuário por ID!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao buscar usuário por ID!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void updateStatus(User user) {
        try {
            entityManager.createNativeQuery(
                    "UPDATE auth_service.users SET active = :active WHERE user_id = :id")
                    .setParameter("active", user.isActive())
                    .setParameter("id", user.getId())
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao ativar/desativar usuário!", e);
        }
    }

    @Override
    @Transactional
    public void updateBlocked(User user) {
        try {
            entityManager.createNativeQuery(
                "UPDATE auth_service.users SET blocked = :blocked, active = :active WHERE user_id = :id")
                .setParameter("blocked", user.isBlocked())
                .setParameter("active", user.isActive())
                .setParameter("id", user.getId())
                .executeUpdate();
                
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao bloquear/desbloquear usuário!", e);
        }
    }

    @Override
    @Transactional
    public void updateActive(User user) {
        try {
            entityManager.createNativeQuery(
                "UPDATE auth_service.users SET active = :active WHERE user_id = :id")
                    .setParameter("active", user.isActive())
                    .setParameter("id", user.getId())
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ativar/desativar usuário!", e);
        }
    }

    @Override
    public List<User> findByRole(UserRole role) {
        try {
            System.out.println("Buscando usuários com role: " + role.name());
            
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT user_id, active, name, cpf, email, role, gender, blocked " +
                "FROM auth_service.users " +
                "WHERE role = :role AND active = true")
                .setParameter("role", role.name())
                .getResultList();
            
            List<User> users = new ArrayList<>();
            
            for (Object[] row : results) {
                User user = new User();
                user.setId((UUID) row[0]);  // user_id
                user.setActive((Boolean) row[1]);  // active
                user.setName((String) row[2]);  // name
                user.setCpf((String) row[3]);  // cpf
                user.setEmail((String) row[4]);  // email
                user.setRole(UserRole.valueOf((String) row[5]));  // role
                user.setGender(Gender.valueOf((String) row[6]));  // gender
                user.setBlocked((Boolean) row[7]);  // blocked
                
                users.add(user);
            }
            
            System.out.println("Encontrados " + users.size() + " usuários com role " + role.name());
            
            return users;
        } catch (PersistenceException e) {
            System.out.println("Erro de persistência ao buscar usuários por role: " + e);
            throw new UserDAOException("Erro de persistência ao buscar usuários no banco de dados",
                Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            System.out.println("Erro inesperado ao buscar usuários: " + e);
            throw new UserDAOException("Erro inesperado ao buscar usuários no banco de dados",
                Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
    
}
