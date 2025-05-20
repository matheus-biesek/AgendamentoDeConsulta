package com.code.java_ee_schedule.adpters.out.persistence;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.code.java_ee_schedule.domain.model.Room;
import com.code.lightquery.LightQuery;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomDAO extends LightQuery<Room> {

    protected RoomDAO() {
        super(Room.class, "schedule_service");
    }

    @SuppressWarnings("unchecked")
    public List<Room> findAllByUserId(UUID userId) {
        List<Object> sqlResult = entityManager.createNativeQuery("SELECT * FROM " + schemaName + ".room WHERE user_id = :userId")
        .setParameter("userId", userId)
        .getResultList();
        return sqlResult.stream()
            .map(row -> (Room) row)
            .collect(Collectors.toList());
    }
}
