package com.code.java_ee_workers.domain.model;

import java.util.UUID;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "patient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    private UUID patient_id;
    private UUID user_id;
    private String allergies;
    private String blood_type;
    private Double height;
    private Boolean active;

    public Patient(UUID patient_id, UUID user_id, String allergies, String blood_type, Double height) {
        this.patient_id = patient_id;
        this.user_id = user_id;
        this.allergies = allergies;
        this.blood_type = blood_type;
        this.height = height;
    }
}
