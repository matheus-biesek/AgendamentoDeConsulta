package com.code.java_ee_auth.domain.model;

import com.code.java_ee_auth.domain.enuns.Gender;
import com.code.java_ee_auth.domain.enuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;

    private String name;

    private boolean active;
    
    private String cpf;
    
    private String email;

    private String password;

    private UserRole role;

    private Gender gender;

    private boolean blocked;

    public User(String name, String cpf, String email, String password, UserRole role, Gender gender) {
        this.password = password;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.role = role;
        this.gender = gender;
    }
}
