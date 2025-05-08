package com.rabbitmq.lib;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import com.rabbitmq.lib.consumer.AbstractRabbitMQConsumer;

@WebListener
public class RabbitMQContextListener implements ServletContextListener {

    @Inject
    @Any
    private Instance<AbstractRabbitMQConsumer> consumers;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        consumers.forEach(AbstractRabbitMQConsumer::start);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        consumers.forEach(AbstractRabbitMQConsumer::stop);
    }
} 