CREATE TABLE auth_service.professional (
    id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL,
    professional_type VARCHAR(50) NOT NULL,
    registry_number VARCHAR(50) NOT NULL,
    specialization VARCHAR(255),
);

CREATE TABLE auth_service.patient (
    id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL
);

CREATE TABLE auth_service.room (
    id BIGINT PRIMARY KEY,
    number char(3) NOT NULL,
    function VARCHAR(20) NOT NULL -- PRIVATE_OFFICE, SURGERY, CONSULTATION
);

CREATE TABLE auth_service.schedule_template (
    id BIGINT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    professional_id BIGINT NOT NULL,
    day_of_week VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (professional_id) REFERENCES professional(id)
);

CREATE TABLE auth_service.time_slot (
    id BIGINT PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    professional_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (professional_id) REFERENCES professional(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE auth_service.appointment (
    id BIGINT PRIMARY KEY,
    time_slot_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    FOREIGN KEY (time_slot_id) REFERENCES time_slot(id),
    FOREIGN KEY (patient_id) REFERENCES patient(id)
);

CREATE TABLE auth_service.appointment_audit (
    id BIGINT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointment(id)
);