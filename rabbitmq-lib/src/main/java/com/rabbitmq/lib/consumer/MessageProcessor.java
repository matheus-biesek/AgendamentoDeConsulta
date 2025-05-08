package com.rabbitmq.lib.consumer;

@FunctionalInterface
public interface MessageProcessor {
    String process(String message);
} 