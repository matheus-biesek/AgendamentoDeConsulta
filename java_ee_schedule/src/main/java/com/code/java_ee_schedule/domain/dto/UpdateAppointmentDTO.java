package com.code.java_ee_schedule.domain.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppointmentDTO {
    private UUID appointmentId;
    private UUID timeSlotId;
    private UUID patientId;
    private boolean active;
    
    public UpdateAppointmentDTO(UUID appointment_id, UUID time_slot_id, UUID patient_id) {
        this.appointmentId = appointment_id;
        this.timeSlotId = time_slot_id;
        this.patientId = patient_id;
    }
}