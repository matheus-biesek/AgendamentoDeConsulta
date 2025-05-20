package com.code.java_ee_schedule.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTimeSlotDTO {
    private UUID timeSlotId;
    private Boolean active;
    private UUID userId;
    private UUID roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime date;
}
