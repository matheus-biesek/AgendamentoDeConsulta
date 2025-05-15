CREATE SCHEMA profile_service;

CREATE TABLE profile_service.professional (
    professional_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    professional_type VARCHAR(50) NOT NULL,
    registry_number VARCHAR(50) NOT NULL,
    specialization VARCHAR(255)
);

CREATE TABLE profile_service.patient (
    patient_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    allergies TEXT,
    blood_type VARCHAR(5),
    weight DECIMAL(5,2),
    height DECIMAL(5,2)
);

CREATE TABLE profile_service.room (
    room_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number char(3) NOT NULL,
    function VARCHAR(20) NOT NULL -- PRIVATE_OFFICE, SURGERY, CONSULTATION
    professional_id UUID, -- NULL indica que a sala está disponível
    CONSTRAINT fk_room_professional
        FOREIGN KEY (professional_id)
        REFERENCES profile_service.professional(professional_id)
);

CREATE INDEX idx_room_professional_id ON profile_service.room(professional_id);
