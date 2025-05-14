package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;
import com.code.java_ee_schedule.domain.model.Appointment;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppointmentDAO extends LightQuery<Appointment> {

    public AppointmentDAO() {
        super(Appointment.class, "schedule_service");
    }
}
