package com.code.java_ee_auth.domain.enuns;

import lombok.Getter;

@Getter
public enum ActionType {
    CREATED ("CREATED"),
    DELETE ("DELETE"),
    UPDATE ("UPDATE"),
    REFRESH ("REFRESH"),
    REVOKED ("REVOKED"),
    EXPIRED ("EXPIRED"),
    TOKEN_BINDING_MISMATCH ("TOKEN_BINDING_MISMATCH"),
    INACTIVE ("INACTIVE");

    private String value;

    ActionType(String value) {
        this.value = value;
    }
}
