package com.rabbitmq.lib.example;

import com.rabbitmq.lib.AbstractRabbitMQConsumer;
import com.rabbitmq.lib.MessageProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("userConsumer")
public class UserConsumer extends AbstractRabbitMQConsumer {

    @Override
    protected String getQueueName() {
        return "user-queue";
    }

    @Override
    protected String getHost() {
        return "localhost";
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
        return message -> {
            // Aqui você implementa sua lógica de processamento
            // Por exemplo, buscar um usuário no banco de dados
            return "Processando mensagem: " + message;
        };
    }
} 