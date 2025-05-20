package com.code.java_ee_schedule.domain.model;

import java.util.UUID;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private UUID appointment_id;
    private UUID time_slot_id;
    private UUID patient_id;
    private boolean active;

    public Appointment(UUID appointment_id, boolean active) {
        this.appointment_id = appointment_id;
        this.active = active;
    }

    public Appointment(UUID appointment_id, UUID time_slot_id, UUID patient_id) {
        this.appointment_id = appointment_id;
        this.time_slot_id = time_slot_id;
        this.patient_id = patient_id;
    }
}
