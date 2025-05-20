package com.code.java_ee_schedule.adpters.in.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.code.java_ee_schedule.adpters.out.persistence.ScheduleTemplateDAO;
import com.code.java_ee_schedule.domain.dto.UpdateScheduleTemplateDTO;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScheduleTemplateService {
    
    @Inject
    private ScheduleTemplateDAO scheduleTemplateDAO;

    public UUID create(UpdateScheduleTemplateDTO dto) {
        UUID id = UUID.randomUUID();
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate(id, dto.getRoomId(), dto.getUserId(), dto.getDayOfWeek().toString(), dto.getStartTime(), dto.getEndTime(), dto.getSlotDurationMinutes());
        scheduleTemplateDAO.create(scheduleTemplate);
        return id;
    }

    public void update(UpdateScheduleTemplateDTO dto) {
        ScheduleTemplate scheduleTemplate;
        if (dto.getDayOfWeek() == null) {
            scheduleTemplate = new ScheduleTemplate(dto.getScheduleTemplateId(), dto.getRoomId(), dto.getUserId(), dto.getStartTime(), dto.getEndTime(), dto.getSlotDurationMinutes());
        }else{
            scheduleTemplate = new ScheduleTemplate(dto.getScheduleTemplateId(), dto.getRoomId(), dto.getUserId(), dto.getDayOfWeek().toString(), dto.getStartTime(), dto.getEndTime(), dto.getSlotDurationMinutes());
        }
        scheduleTemplateDAO.update(scheduleTemplate);
    }

    public List<UpdateScheduleTemplateDTO> findAllByUserId(UUID userId) {
        return scheduleTemplateDAO.findAllByUserId(userId).stream().map(UpdateScheduleTemplateDTO::new).collect(Collectors.toList());
    }

    public List<ScheduleTemplate> findAll() {
        return scheduleTemplateDAO.findAll();
    }

    public ScheduleTemplate findById(UUID id) {
        return scheduleTemplateDAO.findById(id);
    }
}
