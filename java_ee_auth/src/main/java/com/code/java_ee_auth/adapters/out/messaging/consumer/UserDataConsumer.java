package com.code.java_ee_auth.adapters.out.messaging.consumer;

import com.code.java_ee_auth.adapters.out.messaging.processor.UserMessageProcessor;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.rabbitmq.lib.consumer.AbstractRabbitMQConsumer;
import com.rabbitmq.lib.consumer.MessageProcessor;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.Optional;

@ApplicationScoped
@Named("userDataConsumer")
public class UserDataConsumer extends AbstractRabbitMQConsumer {

    @Inject
    private UserDAOImpl userDao;

    @Inject
    private UserMessageProcessor messageProcessor;

    @Override
    protected String getQueueName() {
        return "search-user-data";
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
            Optional<User> userOpt = userDao.findById(UUID.fromString(message.trim()));
            
            return userOpt.map(user -> messageProcessor.processMessageOfUserData(user))
                .orElse("Usuário não encontrado");
        };
    }
}