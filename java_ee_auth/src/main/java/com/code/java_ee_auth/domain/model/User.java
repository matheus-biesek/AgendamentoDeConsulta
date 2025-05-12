package com.code.java_ee_auth.domain.model;

import com.code.java_ee_auth.domain.enuns.Gender;
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

    private Gender gender;

    private boolean blocked;

    public User(String name, String cpf, String email, String password, Gender gender) {
        this.password = password;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.gender = gender;
    }

    public User(UUID id, String name, String cpf, String email, String password, Gender gender) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.gender = gender;
    }
}
