package com.code.java_ee_auth.adapters.persistence;

import com.code.java_ee_auth.adapters.rest.exeception.UserAlreadyExistsException;
import com.code.java_ee_auth.adapters.rest.exeception.UserDAOException;
import com.code.java_ee_auth.adapters.rest.exeception.UserNoExistException;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.domain.port.out.UserDAOPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import java.util.Optional;

@ApplicationScoped
public class UserDAOImpl implements UserDAOPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void create(User userSend) {
        try {
            System.out.println("Username recebido: " + userSend.getUsername());
            System.out.println("Senha (hash): " + userSend.getPassword());
            System.out.println("Role: " + userSend.getRole().name());
            System.out.println("Sex: " + userSend.getSex().name());

            entityManager.createNativeQuery(
                            "INSERT INTO users (username, password, role, sex) VALUES (?, ?, ?::userRole, ?::sexRole)")
                    .setParameter(1, userSend.getUsername()) // Certifique-se que não está criptografado aqui!
                    .setParameter(2, userSend.getPassword()) // Apenas a senha deve estar criptografada
                    .setParameter(3, userSend.getRole().name())
                    .setParameter(4, userSend.getSex().name())
                    .executeUpdate();
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException("O usuário já existe no banco de dados!");
        } catch (DataException e) {
            throw new UserDAOException("Erro: Dados excedem o tamanho permitido!", Response.Status.BAD_REQUEST, e);
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistence ao salvar usuário no banco!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao salvar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }



    @Override
    public Optional<User> findByUsername(String username) {
        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();  // Retorna Optional<User>
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistence ao buscar o usuário no banco de dados", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao buscar usuário no banco de dados", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void update(User user) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                            "UPDATE users SET role = :role WHERE username = :username")
                    .setParameter("role", user.getRole().name())
                    .setParameter("username", user.getUsername())
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new UserNoExistException("Usuário não encontrado para atualização!");
            }
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro de persistência ao atualizar dados do usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        } catch (ConstraintViolationException e) {
            throw new UserDAOException("Violação de restrição no banco de dados ao atualizar usuário!", Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao atualizar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void delete(String username) {
        try {
            int deletedRows = entityManager.createNativeQuery("DELETE FROM users WHERE username = :username")
                    .setParameter("username", username)
                    .executeUpdate();

            if (deletedRows == 0) {
                throw new UserNoExistException("Usuário não encontrado!");
            }
        } catch (PersistenceException e) {
            throw new UserDAOException("Erro ao excluir usuário! Pode haver dependências no banco.", Response.Status.CONFLICT, e);
        } catch (IllegalArgumentException e) {
            throw new UserDAOException("Parâmetro inválido para exclusão!", Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            throw new UserDAOException("Erro inesperado ao deletar usuário!", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
