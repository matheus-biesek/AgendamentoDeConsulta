package com.code.java_ee_auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsersByRoleAndActiveDTO {
    @NotNull
    private String role;

    @NotNull
    private boolean active;
}
