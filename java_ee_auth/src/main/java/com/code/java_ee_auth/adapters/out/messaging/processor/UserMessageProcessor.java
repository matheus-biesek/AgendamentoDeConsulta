package com.code.java_ee_auth.adapters.out.messaging.processor;

import java.util.List;
import com.code.java_ee_auth.domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMessageProcessor {
    
    public String processMessageOfUserData(User user) {
        try {
            return String.format(
                "ID: %s, Nome: %s, CPF: %s, Email: %s, Role: %s, Ativo: %s, Bloqueado: %s",
                user.getId(),
                user.getName(),
                user.getCpf(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.isBlocked()
            );
        } catch (Exception e) {
            return "Erro ao processar a mensagem: " + e.getMessage();
        }
    }

    public String processMessageOfUsersByRole(List<User> users) {
        StringBuilder sb = new StringBuilder();
            for (User user : users) {
                sb.append(String.format(
                    "ID: %s, Nome: %s, CPF: %s, Email: %s, Role: %s, Ativo: %s, Bloqueado: %s\n",
                    user.getId(),
                    user.getName(),
                    user.getCpf(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive(),
                    user.isBlocked()
                ));
            }
            return sb.toString();
    }
} 