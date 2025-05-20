package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;

import java.time.LocalDateTime;
import java.sql.Time;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.code.java_ee_schedule.domain.model.TimeSlot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped

public class TimeSlotDAO extends LightQuery<TimeSlot> {

    public TimeSlotDAO() {
        super(TimeSlot.class, "schedule_service");
    }

    @SuppressWarnings("unchecked")
    public List<TimeSlot> findAllByUserId(UUID userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Object> params = entityManager.createNativeQuery("select * from " + schemaName + ".time_slot where user_id = :userId and start_time between :startTime and :endTime")
            .setParameter("userId", userId)
            .setParameter("startTime", startTime)
            .setParameter("endTime", endTime)
            .getResultList();
            return params.stream().map(TimeSlot.class::cast).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar horários do usuário", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TimeSlot> findAllByUserId(UUID userId, boolean active) {
        try {
            System.out.println("Buscando time slots para userId: " + userId + " e active: " + active);
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT time_slot_id, schedule_template_id, active, user_id, room_id, start_time, end_time, date " +
                "FROM " + schemaName + ".time_slot WHERE user_id = :userId and active = :active")
                .setParameter("userId", userId)
                .setParameter("active", active)
                .getResultList();

            System.out.println("Número de resultados encontrados: " + results.size());

            return results.stream()
                .map(row -> {
                    try {
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setTime_slot_id((UUID) row[0]);
                        timeSlot.setSchedule_template_id((UUID) row[1]);
                        timeSlot.setActive((Boolean) row[2]);
                        timeSlot.setUser_id((UUID) row[3]);
                        timeSlot.setRoom_id((UUID) row[4]);
                        timeSlot.setStart_time((Time) row[5]);
                        timeSlot.setEnd_time((Time) row[6]);
                        
                        // Converter java.sql.Date para LocalDateTime
                        Date sqlDate = (Date) row[7];
                        timeSlot.setDate(sqlDate.toLocalDate().atStartOfDay());
                        
                        return timeSlot;
                    } catch (Exception e) {
                        System.err.println("Erro ao mapear linha: " + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro detalhado ao buscar horários: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar horários do usuário: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TimeSlot> findAllByUserIdAndDate(UUID userId, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
        try {
            System.out.println("Buscando time slots para userId: " + userId + ", entre " + startDate + " e " + endDate + ", active: " + active);
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT time_slot_id, schedule_template_id, active, user_id, room_id, start_time, end_time, date " +
                "FROM " + schemaName + ".time_slot WHERE user_id = :userId and date between :startDate and :endDate and active = :active")
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("active", active)
                .getResultList();

            System.out.println("Número de resultados encontrados: " + results.size());

            return results.stream()
                .map(row -> {
                    try {
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setTime_slot_id((UUID) row[0]);
                        timeSlot.setSchedule_template_id((UUID) row[1]);
                        timeSlot.setActive((Boolean) row[2]);
                        timeSlot.setUser_id((UUID) row[3]);
                        timeSlot.setRoom_id((UUID) row[4]);
                        timeSlot.setStart_time((Time) row[5]);
                        timeSlot.setEnd_time((Time) row[6]);
                        
                        // Converter java.sql.Date para LocalDateTime
                        Date sqlDate = (Date) row[7];
                        timeSlot.setDate(sqlDate.toLocalDate().atStartOfDay());
                        
                        return timeSlot;
                    } catch (Exception e) {
                        System.err.println("Erro ao mapear linha: " + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro detalhado ao buscar horários: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar horários do usuário: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TimeSlot> findAllByRoomIdAndDate(UUID roomId, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
        try {
            List<Object> params = entityManager.createNativeQuery("select * from " + schemaName + ".time_slot where room_id = :roomId and date between :startDate and :endDate and active = :active")
            .setParameter("roomId", roomId)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("active", active)
            .getResultList();
            return params.stream().map(TimeSlot.class::cast).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar horários da sala", e);
        }
    }

    @Transactional
    public void createAll(List<TimeSlot> timeSlots) {
        try {
            for (TimeSlot timeSlot : timeSlots) {
                try {
                    create(timeSlot);
                } catch (Exception e) {
                    System.err.println("Erro ao criar time slot: " + timeSlot);
                    System.err.println("Detalhes do erro: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Erro ao criar horário específico: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro geral ao criar horários: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar horários: " + e.getMessage(), e);
        }
    }

    public void updateAll(List<TimeSlot> timeSlots) {
        try {
            for (TimeSlot timeSlot : timeSlots) {
                update(timeSlot);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar horários", e);
        }
    }
}
