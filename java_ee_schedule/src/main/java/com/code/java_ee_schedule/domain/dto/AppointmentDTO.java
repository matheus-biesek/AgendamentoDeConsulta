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
public class AppointmentDTO {
    private UUID appointmentId;
    private UUID timeSlotId;
    private UUID patientId;
}