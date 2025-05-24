package com.code.java_ee_schedule.adpters.in.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.code.java_ee_schedule.adpters.out.messaging.producer.SearchUserProducer;
import com.code.java_ee_schedule.adpters.out.persistence.RoomDAO;
import com.code.java_ee_schedule.domain.dto.UpdateRoomDTO;
import com.code.java_ee_schedule.domain.model.Room;
import com.rabbitmq.lib.utils.HandleMessage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RoomService {

    @Inject
    private RoomDAO roomDAO;

    @Inject
    private SearchUserProducer searchUserProducer;

    public UUID create(String number, String function, UUID professional_id) {
        UUID roomId = UUID.randomUUID();
        UUID userUuid = null;
        Room room = null;
        if (professional_id != null) {

            String responseUser = searchUserProducer.sendAndReceive(professional_id.toString());
            Map<String, Object> userData = HandleMessage.parseResponseToMap(responseUser);
            
            userUuid = UUID.fromString(userData.get("id").toString());
            if (userUuid == null) {
                throw new RuntimeException("Usuário não encontrado");
            }
            
            room = new Room(roomId, number, function, userUuid);
        } else {
            room = new Room(roomId, number, function);
        }
        roomDAO.create(room);
        return roomId;
    }

    public void updateRoom(Room room) {
        if (room.getUser_id() == null) {
            room.setUser_id(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        }
        roomDAO.update(room);
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = roomDAO.findAll();
        if (rooms.isEmpty()) {
            throw new RuntimeException("Nenhuma sala encontrada");
        }
        return rooms;
    }

    // OFFLINE
    public List<Room> getRoomsByUserId(UUID userId) {
        List<Room> rooms = roomDAO.findAllByUserId(userId);
        if (rooms.isEmpty()) {
            throw new RuntimeException("Nenhuma sala encontrada");
        }
        return rooms;
    }

    // OFFLINE
    public Room getRoomById(UUID id) {
        Room room = roomDAO.findById(id);
        if (room == null) {
            throw new RuntimeException("Sala não encontrada");
        }
        return room;
    }

    public Room search(UUID id) {
        Room room = roomDAO.findById(id);
        if (room == null) {
            throw new RuntimeException("Sala não encontrada");
        }
        return room;
    }
}
