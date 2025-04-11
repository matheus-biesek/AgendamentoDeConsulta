package com.code.java_ee_auth.domain.model;

import com.code.java_ee_auth.domain.enuns.SexRole;
import com.code.java_ee_auth.domain.enuns.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SexRole sex;

    public User() {}

    public User(String username, String password, UserRole role, SexRole sex) {
        this.password = password;
        this.username = username;
        this.role = role;
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public SexRole getSex(){
        return this.sex;
    }

    public void setSex(SexRole sex) {
        this.sex = sex;
    }
}
