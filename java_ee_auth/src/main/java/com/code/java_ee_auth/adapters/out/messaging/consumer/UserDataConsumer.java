package com.code.java_ee_auth.adapters.out.messaging.consumer;

import com.code.java_ee_auth.adapters.out.messaging.processor.UserMessageProcessor;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.adapters.out.persistence.UserDAOImpl;
import com.rabbitmq.lib.AbstractRabbitMQConsumer;
import com.rabbitmq.lib.MessageProcessor;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.Optional;

@ApplicationScoped
@Named("userDataConsumer")
public class UserDataConsumer extends AbstractRabbitMQConsumer {

    private static final String QUEUE_NAME = "search-user-data";
    private static final String HOST = "rabbitmq";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    @Inject
    private UserDAOImpl userDao;

    @Inject
    private UserMessageProcessor messageProcessor;

    @Override
    protected String getQueueName() {
        return QUEUE_NAME;
    }

    @Override
    protected String getHost() {
        return HOST;
    }

    @Override
    protected String getUsername() {
        return USERNAME;
    }

    @Override
    protected String getPassword() {
        return PASSWORD;
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