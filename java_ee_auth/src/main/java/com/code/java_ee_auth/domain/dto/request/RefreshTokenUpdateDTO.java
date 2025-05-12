package com.code.java_ee_auth.domain.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenUpdateDTO {
    @NotNull
    private UUID refreshTokenId;

    private Boolean active;

    public RefreshTokenUpdateDTO(UUID refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }
}
