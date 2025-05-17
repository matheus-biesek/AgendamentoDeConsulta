package com.code.java_ee_workers.domain.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePatientDTO {
    private UUID id;
    private UUID userId;
    private Boolean active;
    private String allergies;
    private String bloodType;
    private Double height;

    public UpdatePatientDTO(UUID id, UUID userId, String allergies, String bloodType, Double height, Boolean active) {
        this.id = id;
        this.userId = userId;
        this.allergies = allergies;
        this.bloodType = bloodType;
        this.height = height;
        this.active = active;
    }
}