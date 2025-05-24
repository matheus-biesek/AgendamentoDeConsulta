package com.code.java_ee_schedule.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserIdDTO {
    @NotNull(message = "O ID do usuário é obrigatório")
    private UUID userId;
}
