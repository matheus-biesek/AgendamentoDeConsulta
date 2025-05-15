package com.code.java_ee_workers.adapters.out.persistence;

import com.code.java_ee_workers.domain.model.Room;
import com.code.lightquery.LightQuery;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomDAO extends LightQuery<Room> {

    protected RoomDAO() {
        super(Room.class, "profile_service");
    }
}
