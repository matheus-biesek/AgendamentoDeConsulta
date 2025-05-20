package com.rabbitmq.lib.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleMessage {

    public static Map<String, Object> parseResponseToMap(String response) {
        try {
            Map<String, Object> userMap = new HashMap<>();
            String[] lines = response.split(",");
            
            for (String line : lines) {
                line = line.trim();
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    if (key.equals("roles")) {
                        // Trata o array de roles
                        value = value.replace("[", "").replace("]", "").trim();
                        String[] roles = value.split(",");
                        List<String> rolesList = new ArrayList<>();
                        for (String role : roles) {
                            rolesList.add(role.trim());
                        }
                        userMap.put(key, rolesList);
                    } else {
                        // Trata valores simples
                        userMap.put(key, value);
                    }
                }
            }
            
            return userMap;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta: " + e.getMessage(), e);
        }
    }
}
