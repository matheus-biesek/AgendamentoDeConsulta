package com.code.java_ee_auth.domain.dto.request;

import com.code.java_ee_auth.domain.enuns.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateRoleDTO {

    @NotBlank(message = "O nome de usuário não pode ser nulo, vazio ou em branco!")
    String username;

    @Pattern(regexp = "ADMIN|PATIENT|NURSE|TECHNICIAN|DOCTOR", message = "O papel de usuário invalido!")
    String role;

    public String getUsername() {
        return this.username;
    }

    public UserRole getRole() {
        if (this.role == null) {
            return null;
        }
        return UserRole.valueOf(this.role.toUpperCase());
    }
}
