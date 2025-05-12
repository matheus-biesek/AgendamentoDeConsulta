package com.code.java_ee_auth.domain.enuns;

import lombok.Getter;

@Getter
public enum Roles {
    ADMIN ("admin"),
    DOCTOR ("doctor"),
    NURSE ("nurse"),
    SECRETARY ("secretary"),
    TECHNICIAN ("technician"),
    PATIENT ("patient");

    private String role;

    Roles(String role) {
        this.role = role;
    }
}
