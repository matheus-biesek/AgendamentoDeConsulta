package com.rabbitmq.lib.example;

import com.rabbitmq.lib.producer.AbstractRabbitMQProducer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimpleProducer extends AbstractRabbitMQProducer {

    @Override
    protected String getQueueName() {
        return "minha-fila";
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
} 