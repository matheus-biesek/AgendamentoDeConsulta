package com.code.java_ee_schedule.adpters.in.service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.time.LocalDate;

import com.code.java_ee_schedule.adpters.out.messaging.producer.SearchUserProducer;
import com.code.java_ee_schedule.adpters.out.persistence.TimeSlotDAO;
import com.code.java_ee_schedule.domain.dto.CreateTimeSlotsDTO;
import com.code.java_ee_schedule.domain.dto.UpdateScheduleTemplateDTO;
import com.code.java_ee_schedule.domain.model.Room;
import com.code.java_ee_schedule.domain.model.ScheduleTemplate;
import com.code.java_ee_schedule.domain.model.TimeSlot;
import com.rabbitmq.lib.utils.HandleMessage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TimeSlotService {

    @Inject
    private TimeSlotDAO timeSlotDAO;
    @Inject
    private ScheduleTemplateService scheduleTemplateService;
    @Inject
    private RoomService roomService;
    @Inject
    private SearchUserProducer searchUserProducer;

    // Futuramente validar se a sala não é de uso privado
    public void createSlots(CreateTimeSlotsDTO dto) {
        // Validação inicial
        UUID professionalId = dto.getUserId();
        UUID roomId = dto.getRoomId();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        validateDates(startDate, endDate);
        validateUserAndRoom(professionalId, roomId);

        // Verificar slots existentes para o profissional
        List<TimeSlot> timeSlotsOfUser = findAllByUserIdAndDate(professionalId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), true);
        if (timeSlotsOfUser.size() > 0) {
            throw new RuntimeException("O profissional já tem um horário em algum dia desse período");
        }
        // Verificar slots existentes para a sala
        List<TimeSlot> timeSlotsOfRoom = findAllByRoomIdAndDate(roomId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), true);
        if (timeSlotsOfRoom.size() > 0) {
            throw new RuntimeException("A sala já tem um horário marcado para esse período");
        }

        // Buscar templates do usuário
        List<UpdateScheduleTemplateDTO> scheduleTemplates = scheduleTemplateService.findAllByUserId(professionalId);
        List<TimeSlot> slotsToCreate = new ArrayList<>();

        // Iterar sobre cada dia no período
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // Filtrar templates para o dia atual
            List<UpdateScheduleTemplateDTO> templatesForDay = scheduleTemplates.stream()
                .filter(template -> isWeekdayInTemplate(template, dayOfWeek))
                .toList();

            // Para cada template do dia
            for (UpdateScheduleTemplateDTO template : templatesForDay) {
                Time startTimeTemplate = template.getStartTime();
                Time endTimeTemplate = template.getEndTime();
                int slotDurationMinutes = template.getSlotDurationMinutes();
                UUID scheduleTemplateId = template.getScheduleTemplateId();

                // Calcular os slots para o dia
                List<TimeSlot> daySlots = calculateSlotsForDay(startTimeTemplate, endTimeTemplate, slotDurationMinutes, scheduleTemplateId);
                
                // Adicionar informações adicionais aos slots
                for (TimeSlot slot : daySlots) {
                    slot.setUser_id(professionalId);
                    slot.setRoom_id(roomId);
                    slot.setDate(currentDate.atStartOfDay());
                    slot.setActive(true);
                    slotsToCreate.add(slot);
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        // Salvar todos os slots criados
        if (!slotsToCreate.isEmpty()) {
            timeSlotDAO.createAll(slotsToCreate);
        }

        // Enviar mensagem para o RabbitMQ para o envio de e-mails
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível criar slots para datas passadas");
        }

        // Limite de 3 meses para criação de slots
        if (end.isAfter(LocalDate.now().plusMonths(3))) {
            throw new IllegalArgumentException("Não é possível criar slots para mais de 3 meses no futuro");
        }
    }

    private void validateUserAndRoom(UUID userId, UUID roomId) {
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório");
        }

        if (roomId == null) {
            throw new IllegalArgumentException("O ID da sala é obrigatório");
        }

        String responseUser = searchUserProducer.sendAndReceive(userId.toString());
        Map<String, Object> userData = HandleMessage.parseResponseToMap(responseUser);
        UUID userUuid = UUID.fromString(userData.get("id").toString());
        if (userUuid == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        // Verifica se a sala existe
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Sala não encontrada");
        }
    }

    private boolean isWeekdayInTemplate(UpdateScheduleTemplateDTO template, DayOfWeek day) {
        // Verifica se o dia da semana está presente no template
        return template.getDayOfWeek().toString().contains(day.toString());
    }

    public void update(TimeSlot timeSlot) {
        timeSlotDAO.update(timeSlot);
    }

    private List<TimeSlot> calculateSlotsForDay(Time startTime, Time endTime, int durationMinutes, UUID scheduleTemplateId) {
        List<TimeSlot> slots = new ArrayList<>();
        
        // Converter Time para minutos para facilitar os cálculos
        int startMinutes = startTime.toLocalTime().toSecondOfDay() / 60;
        int endMinutes = endTime.toLocalTime().toSecondOfDay() / 60;
        
        // Calcular quantos slots cabem no período
        int totalMinutes = endMinutes - startMinutes;
        int numberOfSlots = totalMinutes / durationMinutes;
        
        // Criar os slots
        for (int i = 0; i < numberOfSlots; i++) {
            int slotStartMinutes = startMinutes + (i * durationMinutes);
            int slotEndMinutes = slotStartMinutes + durationMinutes;
            
            // Converter minutos de volta para Time
            Time slotStart = Time.valueOf(String.format("%02d:%02d:00", 
                slotStartMinutes / 60, slotStartMinutes % 60));
            Time slotEnd = Time.valueOf(String.format("%02d:%02d:00", 
                slotEndMinutes / 60, slotEndMinutes % 60));
            
            // Criar o slot
            TimeSlot slot = new TimeSlot();
            slot.setStart_time(slotStart);
            slot.setEnd_time(slotEnd);
            slot.setActive(true);
            slot.setSchedule_template_id(scheduleTemplateId);
            
            slots.add(slot);
        }
        
        return slots;
    }

    public void cancel(UUID timeSlotId, boolean active) {
        TimeSlot timeSlot = new TimeSlot(timeSlotId, active);
        timeSlotDAO.update(timeSlot);
    }

    public List<TimeSlot> findAll() {
        return timeSlotDAO.findAll();
    }

    public TimeSlot findById(UUID id) {
        return timeSlotDAO.findById(id);
    }

    public List<TimeSlot> findAllByUserId(UUID userId, boolean active) {
        return timeSlotDAO.findAllByUserId(userId, active);
    }

    public List<TimeSlot> findAllByUserIdAndDate(UUID userId, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
        return timeSlotDAO.findAllByUserIdAndDate(userId, startDate, endDate, active);
    }

    public List<TimeSlot> findAllByRoomIdAndDate(UUID roomId, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
        return timeSlotDAO.findAllByRoomIdAndDate(roomId, startDate, endDate, active);
    }
}
