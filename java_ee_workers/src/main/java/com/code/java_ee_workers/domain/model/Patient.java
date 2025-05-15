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
    private Double weight;
    private Double height;
    private Boolean active;
}
