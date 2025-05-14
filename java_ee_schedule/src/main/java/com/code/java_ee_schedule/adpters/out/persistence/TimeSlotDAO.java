package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;
import com.code.java_ee_schedule.domain.model.TimeSlot;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

public class TimeSlotDAO extends LightQuery<TimeSlot> {

    public TimeSlotDAO() {
        super(TimeSlot.class, "schedule_service");
    }
}
