package com.code.java_ee_auth.adapters.out.messaging.consumer;

import com.code.java_ee_auth.adapters.out.messaging.processor.UserMessageProcessor;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.rabbitmq.lib.consumer.AbstractRabbitMQConsumer;
import com.rabbitmq.lib.consumer.MessageProcessor;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.List;

@ApplicationScoped
@Named("searchUsersByRoleConsumer")
public class SearchUsersByRoleConsumer extends AbstractRabbitMQConsumer {


    @Inject
    private UserDAOImpl userDao;

    @Inject
    private UserMessageProcessor messageProcessor;

    @Override
    protected String getQueueName() {
        return "search-users-by-role";
    }

    @Override
    protected String getHost() {
        return "rabbitmq";
    }

    @Override
    protected String getUsername() {
        return "guest";
    }

    @Override
    protected String getPassword() {
        return "guest";
    }

    @Override
    protected MessageProcessor getMessageProcessor() {
        return (message) -> {
            try {
                UserRole role = UserRole.valueOf(message.trim().toUpperCase());
                List<User> users = userDao.findByRole(role);
                
                if (!users.isEmpty()) {
                    return messageProcessor.processMessageOfUsersByRole(users);
                } else {
                    return "Nenhum usuário encontrado com a role: " + role;
                }
            } catch (IllegalArgumentException e) {
                return "Role inválida: " + message;
            }
        };
    }
}
