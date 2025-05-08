package com.rabbitmq.lib.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.lib.RabbitMQConnectionService;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractRabbitMQConsumer {

    @Inject
    protected RabbitMQConnectionService connectionService;
    
    @Inject
    protected MessageHandler messageHandler;
    
    protected abstract String getQueueName();
    protected abstract String getHost();
    protected abstract String getUsername();
    protected abstract String getPassword();
    protected abstract MessageProcessor getMessageProcessor();

    public void start() {
        try {
            connectionService.connect(getHost(), getUsername(), getPassword(), getQueueName());
            Channel channel = connectionService.getChannel();

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    messageHandler.handleMessage(delivery, channel, getMessageProcessor());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            channel.basicConsume(getQueueName(), false, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            connectionService.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 