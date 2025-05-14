package com.code.java_ee_schedule.adpters.out.persistence;

import com.code.lightquery.LightQuery;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

public class ScheduleTemplateDAO extends LightQuery<ScheduleTemplate> {
        
    public ScheduleTemplateDAO() {
        super(ScheduleTemplate.class, "schedule_service");
    }   
}
