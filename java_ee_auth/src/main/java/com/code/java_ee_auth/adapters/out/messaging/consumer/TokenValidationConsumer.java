package com.code.java_ee_auth.adapters.out.messaging.consumer;

import com.code.java_ee_auth.adapters.in.services.security.AccessTokenService;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.rabbitmq.client.*;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@WebListener
public class TokenValidationConsumer implements ServletContextListener {

    // CRIAR ARQUIVO DE CONFIGURAÇÃO PARA RABBITMQ
    private static final String QUEUE_NAME = "minha_fila";
    private static final String HOST = "rabbitmq";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private Connection connection;
    private Channel channel;

    @Inject
    private AccessTokenService accessTokenService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                AMQP.BasicProperties props = delivery.getProperties();
                String replyTo = props.getReplyTo();
                String correlationId = props.getCorrelationId();

                if (replyTo == null || correlationId == null) {
                    return;
                }

                String response;
                try {
                    String[] parts = message.split(":");
                    if (parts.length == 2) {
                        String token = parts[0].trim();
                        String roleString = parts[1].trim();
                        UserRole role = UserRole.valueOf(roleString);

                        boolean isValid = accessTokenService.validateTokenRole(token, role);
                        response = isValid ? "VALID" : "INVALID";
                    } else {
                        response = "Formato de mensagem inválido.";
                    }
                } catch (Exception e) {
                    response = "Erro ao processar a mensagem: " + e.getMessage();
                }

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(correlationId)
                        .build();

                channel.basicPublish("", replyTo, replyProps, response.getBytes(StandardCharsets.UTF_8));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };

            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}