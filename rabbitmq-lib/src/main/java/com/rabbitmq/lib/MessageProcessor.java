package com.rabbitmq.lib;

@FunctionalInterface
public interface MessageProcessor {
    String process(String message);
} 