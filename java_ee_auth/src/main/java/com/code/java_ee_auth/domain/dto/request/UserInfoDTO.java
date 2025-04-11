package com.code.java_ee_auth.domain.dto.request;

import com.code.java_ee_auth.domain.enuns.SexRole;
import com.code.java_ee_auth.domain.enuns.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserInfoDTO {

    @NotBlank(message = "O nome de usuário não pode ser nulo, vazio ou em branco!")
    @Size(min = 3, max = 15, message = "O nome de usuário deve ter entre 3 e 15 caracteres!")
    private String username;

    @NotBlank(message = "A senha não pode ser vazia!")
    @Size(min = 2, message = "A senha deve ter no mínimo 8 caracteres!")
    private String password;

    @NotNull(message = "O papel do usuário (role) não pode ser nulo!")
    private UserRole role;

    @NotNull(message = "O sexo do usuário (sex) não pode ser nulo!")
    private SexRole sex;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public SexRole getSex() {
        return sex;
    }
}
