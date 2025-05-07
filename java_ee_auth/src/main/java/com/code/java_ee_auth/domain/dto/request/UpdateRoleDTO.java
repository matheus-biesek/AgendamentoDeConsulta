package com.code.java_ee_auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateRoleDTO {

    @NotBlank(message = "O nome de usuário não pode ser nulo, vazio ou em branco!")
    String cpf;

    @Pattern(regexp = "ADMIN|SECRETARY|PATIENT|NURSE|TECHNICIAN|DOCTOR", message = "O papel de usuário invalido!")
    String role;
}
