package com.code.java_ee_schedule.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeSlotsDTO {
    private UUID userId;
    private UUID roomId;
    private LocalDate startDate;
    private LocalDate endDate;
}
