package com.code.java_ee_schedule.adpters.in.rest;

import java.util.List;

import com.code.java_ee_schedule.adpters.out.persistence.AppointmentDAO;
import com.code.java_ee_schedule.adpters.out.persistence.ScheduleTemplateDAO;
import com.code.java_ee_schedule.adpters.out.persistence.TimeSlotDAO;
import com.code.java_ee_schedule.domain.model.Appointment;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;
import com.code.java_ee_schedule.domain.model.TimeSlot;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/admin")
public class AdminRest {

    @Inject
    private ScheduleTemplateDAO scheduleTemplateDAO;
    @Inject
    private TimeSlotDAO timeSlotDAO;
    @Inject
    private AppointmentDAO appointmentDAO;

    private void exibirInformacoesEntidades(List<ScheduleTemplate> templates, List<TimeSlot> slots, List<Appointment> appointments) {
        System.out.println("\n=== TEMPLATES DE AGENDAMENTO ===");
        templates.forEach(template -> {
            System.out.println("\nTemplate ID: " + template.getSchedule_template_id());
            System.out.println("Dia da Semana: " + template.getDay_of_week());
            System.out.println("Horário Início: " + template.getStart_time());
            System.out.println("Horário Fim: " + template.getEnd_time());
            System.out.println("Duração do Slot (minutos): " + template.getSlot_duration_minutes());
            System.out.println("------------------------");
        });

        System.out.println("\n=== HORÁRIOS DISPONÍVEIS ===");
        slots.forEach(slot -> {
            System.out.println("\nSlot ID: " + slot.getTime_slot_id());
            System.out.println("Ativo: " + slot.getActive());
            System.out.println("Horário Início: " + slot.getStart_time());
            System.out.println("Horário Fim: " + slot.getEnd_time());
            System.out.println("------------------------");
        });

        System.out.println("\n=== AGENDAMENTOS ===");
        appointments.forEach(appointment -> {
            System.out.println("\nAgendamento ID: " + appointment.getAppointment_id());
            System.out.println("ID do Slot: " + appointment.getTime_slot_id());
            System.out.println("ID do Paciente: " + appointment.getPatient_id());
            System.out.println("------------------------");
        });
    }

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String publicc() {
        List<ScheduleTemplate> scheduleTemplates = scheduleTemplateDAO.findAll();
        List<TimeSlot> timeSlots = timeSlotDAO.findAll();
        List<Appointment> appointments = appointmentDAO.findAll();

        ScheduleTemplate scheduleTemplateAllow = scheduleTemplates.get(0);
        TimeSlot timeSlotAllow = timeSlots.get(0);
        Appointment appointmentAllow = appointments.get(0);

        ScheduleTemplate scheduleTemplate2 = scheduleTemplateDAO.findById(scheduleTemplateAllow.getSchedule_template_id());
        TimeSlot timeSlot2 = timeSlotDAO.findById(timeSlotAllow.getTime_slot_id());
        Appointment appointment2 = appointmentDAO.findById(appointmentAllow.getAppointment_id());

        exibirInformacoesEntidades(scheduleTemplates, timeSlots, appointments);

        System.out.println("\n=== DETALHES DE ENTIDADES ESPECÍFICAS ===");
        System.out.println("\nTemplate Específico:");
        System.out.println(scheduleTemplate2);
        System.out.println("\nHorário Específico:");
        System.out.println(timeSlot2);
        System.out.println("\nAgendamento Específico:");
        System.out.println(appointment2);

        return "Endpoint publico";
    }

    @GET
    @Path("/private")
    @Produces("text/plain")
    public String privatee() {
        return "Endpoint de admin";
    }
}