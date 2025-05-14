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
    private UUID professional_id;
    private String day_of_week;
    private Time start_time;
    private Time end_time;
    private int slot_duration_minutes;
}
