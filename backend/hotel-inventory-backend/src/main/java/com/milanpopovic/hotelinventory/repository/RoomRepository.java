package com.milanpopovic.hotelinventory.repository;

import com.milanpopovic.hotelinventory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByName(String name);
}
