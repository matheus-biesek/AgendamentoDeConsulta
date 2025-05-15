package com.code.java_ee_auth.adapters.out.messaging.processor;

import java.util.List;
import com.code.java_ee_auth.domain.model.User;
import com.code.java_ee_auth.adapters.in.services.user.UserRoleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserMessageProcessor {
    
    @Inject
    private UserRoleService userRoleService;
    
    public String processMessageOfUserData(User user) {
        try {
            List<String> roles = userRoleService.getRolesByUserId(user.getId());
            return String.format(
                "\tid=%s,\n" +
                "\tblocked=%s,\n" +
                "\tactive=%s,\n" +
                "\tname=%s,\n" +
                "\tcpf=%s,\n" +
                "\temail=%s,\n" +
                "\tgender=%s,\n" +
                "\troles=%s\n",
                user.getId(),
                user.isBlocked(),
                user.isActive(),
                user.getName(),
                user.getCpf(),
                user.getEmail(),
                user.getGender(),
                roles
            );
        } catch (Exception e) {
            return "Erro ao processar a mensagem: " + e.getMessage();
        }
    }

    public String processMessageOfUsersByRole(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(processMessageOfUserData(user)).append(",\n");
        }
        return sb.toString();
    }
} 