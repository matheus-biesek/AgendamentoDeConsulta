package com.code.java_ee_auth.domain.dto.request;

import java.util.UUID;

import com.code.java_ee_auth.domain.enuns.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangeDataUserDTO {

    @NotBlank(message = "O nome de usuário não pode ser nulo, vazio ou em branco!")
    @Size(min = 3, max = 15, message = "O nome de usuário deve ter entre 3 e 15 caracteres!")
    private String name;

    @NotNull(message = "O sexo do usuário (gender) não pode ser nulo!")
    private Gender gender;

    @NotBlank(message = "O CPF não pode ser nulo, vazio ou em branco!")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres!")
    private String cpf;

    @NotBlank(message = "O email não pode ser nulo, vazio ou em branco!")
    private String email;

    @NotNull(message = "O ID do usuário não pode ser nulo!")
    private UUID id;
}
