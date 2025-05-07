package com.rabbitmq.lib.example;

import com.rabbitmq.lib.AbstractRabbitMQProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("simpleProducer")
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