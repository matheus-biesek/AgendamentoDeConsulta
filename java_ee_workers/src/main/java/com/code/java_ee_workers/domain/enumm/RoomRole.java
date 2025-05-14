package com.code.java_ee_workers.domain.enumm;

import lombok.Getter;

@Getter
public enum RoomRole {
    PRIVATE_OFFICE ("private_office"),
    SURGERY ("surgery"),
    CONSULTATION ("consultation");

    private String role;

    RoomRole(String role) {
        this.role = role;
    }
}
