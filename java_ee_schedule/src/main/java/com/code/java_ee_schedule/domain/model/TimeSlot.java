package com.code.java_ee_schedule.domain.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    private UUID time_slot_id;
    private Boolean active;
    private UUID professional_id;
    private UUID room_id;
    private Time start_time;
    private Time end_time;
    private LocalDateTime date;
}
