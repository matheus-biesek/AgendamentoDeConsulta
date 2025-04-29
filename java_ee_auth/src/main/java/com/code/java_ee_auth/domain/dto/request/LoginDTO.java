package com.code.java_ee_auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginDTO {
    
    @NotBlank(message = "O CPF não pode ser nulo, vazio ou em branco!")
    private String cpf;

    @NotBlank(message = "A senha não pode ser nula, vazia ou em branco!")
    private String password;
}
