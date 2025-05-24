package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.code.java_ee_schedule.domain.model.Appointment;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppointmentDAO extends LightQuery<Appointment> {

    public AppointmentDAO() {
        super(Appointment.class, "schedule_service");
    }

    @SuppressWarnings("unchecked")
    public List<Appointment> findByUserId(UUID userId) {
        try {
            List<Object[]> result = entityManager.createNativeQuery("SELECT * FROM " + schemaName + "." + tableName + " WHERE patient_id = :userId")
                .setParameter("userId", userId)
                .getResultList();
            return result.stream()
                .map(row -> {
                    try {
                        Appointment appointment = new Appointment();
                        appointment.setAppointment_id((UUID) row[0]);
                        appointment.setTime_slot_id((UUID) row[1]);
                        appointment.setPatient_id((UUID) row[2]);
                        appointment.setActive((Boolean) row[3]);
                        return appointment;
                    } catch (Exception e) {
                        System.err.println("Erro ao mapear linha: " + e.getMessage());
                        return null;
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar agendamentos por usuário", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Appointment> findAppointmentsByProfessionalId(UUID professionalId) {
        try {
            String query = "SELECT a.* FROM " + schemaName + "." + tableName + " a " +
                          "INNER JOIN schedule_service.time_slot ts ON a.time_slot_id = ts.time_slot_id " +
                          "WHERE ts.user_id = :professionalId";
            
            List<Object[]> result = entityManager.createNativeQuery(query)
                .setParameter("professionalId", professionalId)
                .getResultList();
                
            return result.stream()
                .map(row -> {
                    try {
                        Appointment appointment = new Appointment();
                        appointment.setAppointment_id((UUID) row[0]);
                        appointment.setTime_slot_id((UUID) row[1]);
                        appointment.setPatient_id((UUID) row[2]);
                        appointment.setActive((Boolean) row[3]);
                        return appointment;
                    } catch (Exception e) {
                        System.err.println("Erro ao mapear linha: " + e.getMessage());
                        return null;
                    }
                })
                .filter(appointment -> appointment != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar agendamentos por profissional", e);
        }
    }
}
