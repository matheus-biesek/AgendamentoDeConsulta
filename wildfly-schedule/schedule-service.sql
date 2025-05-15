CREATE TABLE schedule_service.schedule_template (
    schedule_template_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL,
    professional_id UUID NOT NULL,
    day_of_week VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL,
    FOREIGN KEY (professional_id) REFERENCES professional(id)
);

CREATE TABLE schedule_service.time_slot (
    time_slot_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    professional_id UUID NOT NULL,
    room_id UUID NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    date DATE NOT NULL,
    
);

CREATE TABLE schedule_service.appointment (
    appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    time_slot_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    FOREIGN KEY (time_slot_id) REFERENCES schedule_service.time_slot(time_slot_id)
);
