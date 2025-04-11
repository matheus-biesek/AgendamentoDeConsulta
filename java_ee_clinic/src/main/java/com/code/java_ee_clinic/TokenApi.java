package com.code.java_ee_clinic;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


@Path("/token")
public class TokenApi {
    private static final String QUEUE_NAME = "minha_fila";

    @POST
    @Path("/validate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateToken(tokenRoleDTO tokenRoleDTO) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq");
            factory.setUsername("guest");
            factory.setPassword("guest");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                String correlationId = UUID.randomUUID().toString();
                String replyQueueName = channel.queueDeclare("", false, true, true, null).getQueue();

                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .correlationId(correlationId)
                        .replyTo(replyQueueName)
                        .build();

                String message = tokenRoleDTO.token() + ":" + tokenRoleDTO.role();
                channel.basicPublish("", QUEUE_NAME, props, message.getBytes(StandardCharsets.UTF_8));

                final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

                String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                    if (correlationId.equals(delivery.getProperties().getCorrelationId())) {
                        response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                    }
                }, consumerTag -> {
                });

                String result = response.poll(10, TimeUnit.SECONDS);
                channel.basicCancel(ctag);

                if (result == null) {
                    return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Timeout ao aguardar resposta").build();
                }

                return Response.ok(result).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao enviar token e role").build();
        }
    }
}
