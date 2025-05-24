CREATE TABLE schedule_service.schedule_template (
    schedule_template_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    day_of_week VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL,
);

CREATE TABLE schedule_service.time_slot (
    time_slot_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_template_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id UUID NOT NULL,
    room_id UUID NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (schedule_template_id) REFERENCES schedule_service.schedule_template(schedule_template_id)
);

CREATE TABLE schedule_service.appointment (
    appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    time_slot_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (time_slot_id) REFERENCES schedule_service.time_slot(time_slot_id)
);

CREATE TABLE profile_service.room (
    room_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number char(3) NOT NULL,
    function VARCHAR(20) NOT NULL, -- PRIVATE_OFFICE, SURGERY, CONSULTATION
    user_id UUID -- NULL indica que a sala está disponível
);

-- SELECT * FROM auth_service.users;
-- SELECT * FROM schedule_service.room;
/*DELETE FROM schedule_service.room
WHERE "function" = 'CONSULTATION';
*/
-- DROP TABLE schedule_service.schedule_template;
-- DROP TABLE schedule_service.appointment;
-- DROP TABLE schedule_service.time_slot;
/*
CREATE TABLE schedule_service.schedule_template (
    schedule_template_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    day_of_week VARCHAR(50) NOT NULL UNIQUE,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL
);
*/

-- SELECT * FROM auth_service.users;
-- SELECT * FROM schedule_service.room;
/*
CREATE TABLE schedule_service.time_slot (
    time_slot_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_template_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id UUID NOT NULL,
    room_id UUID NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (schedule_template_id) REFERENCES schedule_service.schedule_template(schedule_template_id)
);
*/

-- SELECT * FROM schedule_service.time_slot;
--DELETE FROM schedule_service.time_slot;

--SELECT * FROM auth_service.users;

/*
CREATE TABLE schedule_service.appointment (
    appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    time_slot_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (time_slot_id) REFERENCES schedule_service.time_slot(time_slot_id)
);
*/


-- SELECT * FROM schedule_service.time_slot;
-- SELECT * FROM schedule_service.appointment;
-- DELETE FROM schedule_service.appointment;
-- DELETE FROM schedule_service.time_slot;
