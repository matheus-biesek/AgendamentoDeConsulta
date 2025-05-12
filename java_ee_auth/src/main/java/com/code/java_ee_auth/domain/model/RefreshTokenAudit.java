package com.code.java_ee_auth.domain.model;

import java.util.UUID;

import com.code.java_ee_auth.domain.enuns.ActionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenAudit {
    private UUID id;
    private UUID refreshTokenId;
    private ActionType actionType;
    private UUID changedBy;
}
