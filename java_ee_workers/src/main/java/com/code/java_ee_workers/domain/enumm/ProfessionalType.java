package com.code.java_ee_workers.domain.enumm;

import lombok.Getter;

@Getter
public enum ProfessionalType {
    ADMIN("admin"),
    DOCTOR("doctor"), 
    NURSE("nurse"),
    SECRETARY ("secretary"),
    TECHNICIAN ("technician"),
    PATIENT ("patient");

    private String role;

    ProfessionalType(String role) {
        this.role = role;
    }
}