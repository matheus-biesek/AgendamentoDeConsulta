package com.code.java_ee_auth.domain.dto.request;

import java.util.UUID;

import com.code.java_ee_auth.domain.enuns.Gender;
import com.code.java_ee_auth.domain.enuns.Roles;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private UUID userId;

    @NotBlank(message = "O CPF não pode ser nulo, vazio ou em branco!")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres!")
    private String cpf;

    private String name;

    private String email;

    private Gender gender;

    private Roles role;

    private Boolean active;

    private Boolean blocked;
}