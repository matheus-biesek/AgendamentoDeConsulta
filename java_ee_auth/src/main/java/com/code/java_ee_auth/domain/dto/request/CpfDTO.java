package com.code.java_ee_auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class CpfDTO {
    @NotBlank(message = "O CPF não pode ser nulo, vazio ou em branco!")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres!")
    private String cpf;
}
