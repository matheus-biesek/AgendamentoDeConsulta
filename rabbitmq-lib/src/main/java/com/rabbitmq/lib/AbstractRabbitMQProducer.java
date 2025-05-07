package com.rabbitmq.lib;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public abstract class AbstractRabbitMQProducer {
    private Connection connection;
    private Channel channel;
    private String replyQueueName;

    protected abstract String getQueueName();
    protected abstract String getHost();
    protected abstract String getUsername();
    protected abstract String getPassword();

    public String sendAndReceive(String message) {
        try {
            initialize();
            String response = sendMessage(message);
            close();
            return response;
        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException("Erro ao enviar mensagem: " + e.getMessage(), e);
        }
    }

    private void initialize() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHost());
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());

        connection = factory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    private String sendMessage(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", getQueueName(), props, message.getBytes(StandardCharsets.UTF_8));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {});

        return response.take();
    }

    private void close() throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
} 