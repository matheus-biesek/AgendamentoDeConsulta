package com.code.java_ee_auth.adapters.out.messaging.consumer;

import com.code.java_ee_auth.adapters.in.services.user.UserCrudServiceImpl;
import com.code.java_ee_auth.adapters.out.messaging.processor.UserMessageProcessor;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import com.code.java_ee_auth.domain.model.User;
import com.rabbitmq.lib.consumer.AbstractRabbitMQConsumer;
import com.rabbitmq.lib.consumer.MessageProcessor;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("searchUsersByRoleConsumer")
public class SearchUsersByRoleConsumer extends AbstractRabbitMQConsumer {

    @Inject
    private UserCrudServiceImpl userCrudService;

    @Inject
    private UserMessageProcessor messageProcessor;

    @Override
    protected String getQueueName() {
        return "search-users-by-role";
    }

    @Override
    protected String getHost() {
        return "rabbitmq";
    }

    @Override
    protected String getUsername() {
        return "guest";
    }

    @Override
    protected String getPassword() {
        return "guest";
    }

    @Override
    protected MessageProcessor getMessageProcessor() {
        return (message) -> {
            try {
                String role = message.trim().toLowerCase();
                List<UserDataDTO> users = userCrudService.getUsersByRole(role);
                
                if (!users.isEmpty()) {
                    return users.stream()
                        .map(userDTO -> {
                            User user = new User();
                            user.setId(userDTO.id());
                            user.setBlocked(userDTO.blocked());
                            user.setActive(userDTO.active());
                            user.setName(userDTO.name());
                            user.setCpf(userDTO.cpf());
                            user.setEmail(userDTO.email());
                            user.setGender(userDTO.gender());
                            return messageProcessor.processMessageOfUserData(user);
                        })
                        .collect(Collectors.joining(",\n"));
                } else {
                    return "Nenhum usuário encontrado com a role: " + role;
                }
            } catch (IllegalArgumentException e) {
                return "Role inválida: " + message;
            }
        };
    }
}
