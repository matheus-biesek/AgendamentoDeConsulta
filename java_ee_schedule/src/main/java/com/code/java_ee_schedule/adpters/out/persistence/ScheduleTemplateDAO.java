package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;
import java.sql.Time;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.code.java_ee_schedule.domain.model.ScheduleTemplate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

public class ScheduleTemplateDAO extends LightQuery<ScheduleTemplate> {
        
    public ScheduleTemplateDAO() {
        super(ScheduleTemplate.class, "schedule_service");
    }   

    @SuppressWarnings("unchecked")
    public List<ScheduleTemplate> findAllByUserId(UUID userId) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT schedule_template_id, room_id, user_id, day_of_week, start_time, end_time, slot_duration_minutes " +
                "FROM " + schemaName + "." + tableName + " WHERE user_id = :userId")
                .setParameter("userId", userId)
                .getResultList();

            return results.stream()
                .map(row -> {
                    ScheduleTemplate template = new ScheduleTemplate();
                    template.setSchedule_template_id((UUID) row[0]);
                    template.setRoom_id((UUID) row[1]);
                    template.setUser_id((UUID) row[2]);
                    template.setDay_of_week((String) row[3]);
                    template.setStart_time((Time) row[4]);
                    template.setEnd_time((Time) row[5]);
                    template.setSlot_duration_minutes((Integer) row[6]);
                    return template;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar templates do usuário", e);
        }
    }
}
