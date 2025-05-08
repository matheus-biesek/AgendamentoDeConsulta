package com.code.java_ee_workers;

import com.rabbitmq.lib.AbstractRabbitMQProducer;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchUserByRoleProducer extends AbstractRabbitMQProducer{

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

}
