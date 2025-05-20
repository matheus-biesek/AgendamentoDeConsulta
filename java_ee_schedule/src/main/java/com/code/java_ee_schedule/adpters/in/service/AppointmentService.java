package com.code.java_ee_schedule.adpters.in.service;

import java.util.List;
import java.util.UUID;

import com.code.java_ee_schedule.adpters.out.persistence.AppointmentDAO;
import com.code.java_ee_schedule.domain.dto.UpdateAppointmentDTO;
import com.code.java_ee_schedule.domain.model.Appointment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AppointmentService {

    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private AppointmentDAO appointmentDAO;

    public UUID create(UUID timeSlotId, UUID patientId) {
        UUID id = UUID.randomUUID();
        Appointment appointment = new Appointment(id, timeSlotId, patientId, true);
        appointmentDAO.create(appointment);
        timeSlotService.cancel(timeSlotId, false);
        return id;
    }

    public void cancel(UUID appointmentId) {
        Appointment appointment = new Appointment(appointmentId, false);
        appointmentDAO.update(appointment);
    }

    public List<Appointment> findAll() {
        return appointmentDAO.findAll();
    }

    public Appointment findById(UUID id) {
        return appointmentDAO.findById(id);
    }
}
