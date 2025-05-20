package com.code.java_ee_schedule.domain.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Time;
import java.util.UUID;

@Table(name = "schedule_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTemplate {
    private UUID schedule_template_id;
    private UUID room_id;
    private UUID user_id;
    private String day_of_week;
    private Time start_time;
    private Time end_time;
    private int slot_duration_minutes;

    public ScheduleTemplate( UUID scheduleTemplateId  , UUID room_id, UUID user_id, Time start_time, Time end_time, int slot_duration_minutes) {
        this.schedule_template_id = scheduleTemplateId;
        this.room_id = room_id;
        this.user_id = user_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.slot_duration_minutes = slot_duration_minutes;
    }
}
