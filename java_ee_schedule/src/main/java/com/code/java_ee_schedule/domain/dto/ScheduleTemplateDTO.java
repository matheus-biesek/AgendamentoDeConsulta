package com.code.java_ee_schedule.domain.dto;

import java.sql.Time;
import java.util.UUID;

import com.code.java_ee_schedule.domain.enumm.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTemplateDTO {

    private UUID scheduleTemplateId;
    
    private UUID roomId;
    
    private UUID professionalId;
    
    private DayOfWeek dayOfWeek;
    
    private Time startTime;
    
    private Time endTime;
    
    private int slotDurationMinutes;
}