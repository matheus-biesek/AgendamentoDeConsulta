package com.code.java_ee_schedule.domain.dto;

import java.util.UUID;

import com.code.java_ee_schedule.domain.enumm.RoomRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomDTO {
    private UUID id;
    private String number;
    private RoomRole function;
    private UUID userId;

    public UpdateRoomDTO(UUID id, String number, RoomRole function) {
        this.id = id;
        this.number = number;
        this.function = function;
    }
}