package com.code.java_ee_schedule.domain.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private UUID room_id;
    private String number;
    private String function;
    private UUID user_id;

    public Room(UUID room_id, String number, String function) {
        this.room_id = room_id;
        this.number = number;
        this.function = function;
    }
}