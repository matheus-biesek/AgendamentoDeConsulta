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
    private Double weight;
    private Double height;
}