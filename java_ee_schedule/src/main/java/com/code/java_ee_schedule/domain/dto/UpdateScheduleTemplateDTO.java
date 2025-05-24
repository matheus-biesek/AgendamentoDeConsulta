package com.code.java_ee_schedule.domain.dto;

import java.sql.Time;
import java.util.UUID;

import com.code.java_ee_schedule.domain.enumm.DayOfWeek;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleTemplateDTO {

    private UUID scheduleTemplateId;
    
    private UUID roomId;
    
    private UUID userId;
    
    private DayOfWeek dayOfWeek;
    
    private Time startTime;
    
    private Time endTime;
    
    private int slotDurationMinutes;

    public UpdateScheduleTemplateDTO(ScheduleTemplate template) {
        this.scheduleTemplateId = template.getSchedule_template_id();
        this.roomId = template.getRoom_id();
        this.userId = template.getUser_id();
        this.dayOfWeek = DayOfWeek.valueOf(template.getDay_of_week().toUpperCase());
        this.startTime = template.getStart_time();
        this.endTime = template.getEnd_time();
        this.slotDurationMinutes = template.getSlot_duration_minutes();
    }

    public UpdateScheduleTemplateDTO(UUID schedule_template_id, UUID room_id, UUID user_id, String day_of_week,
        Time start_time, Time end_time, int slot_duration_minutes) {
        this.scheduleTemplateId = schedule_template_id;
        this.roomId = room_id;
        this.userId = user_id;
        this.dayOfWeek = DayOfWeek.valueOf(day_of_week.toUpperCase());
        this.startTime = start_time;
        this.endTime = end_time;
        this.slotDurationMinutes = slot_duration_minutes;
    }
    
}

