package com.code.java_ee_workers.domain.dto.request;

import java.util.UUID;

import com.code.java_ee_workers.domain.enumm.RoomRole;

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
    private UUID professionalId;
}