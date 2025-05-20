package com.code.java_ee_schedule.adpters.out.messaging.producer;

import com.rabbitmq.lib.producer.AbstractRabbitMQProducer;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchUserProducer extends AbstractRabbitMQProducer{

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
    
}


