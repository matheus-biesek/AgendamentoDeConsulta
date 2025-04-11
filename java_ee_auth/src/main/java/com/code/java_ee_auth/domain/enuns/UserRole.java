package com.code.java_ee_auth.domain.enuns;

public enum UserRole {
    ADMIN("admin"),
    PATIENT("patient"),
    DOCTOR("doctor"),
    NURSE("nurse"),
    TECHNICIAN("technician"),
    SECRETARY("secretary"),;

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
