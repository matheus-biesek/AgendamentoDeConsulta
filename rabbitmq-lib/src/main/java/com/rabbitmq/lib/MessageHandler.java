package com.rabbitmq.lib;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class MessageHandler {
    
    public void handleMessage(Delivery delivery, Channel channel, MessageProcessor processor) throws IOException {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        
        AMQP.BasicProperties props = delivery.getProperties();
        String replyTo = props.getReplyTo();
        String correlationId = props.getCorrelationId();
        
        if (replyTo == null || correlationId == null) {
            return;
        }
        
        String response = processor.process(message);
        
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
            .correlationId(correlationId)
            .build();
            
        channel.basicPublish("", replyTo, replyProps, response.getBytes(StandardCharsets.UTF_8));
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    }
} 