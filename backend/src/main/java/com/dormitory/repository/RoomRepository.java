package com.dormitory.repository;

import com.dormitory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByDormitory_Name(String dormitoryName);
    List<Room> findByDormitory_Id(Long dormitoryId);

    List<Room> findByDormitory_NameAndFloor(String dormitoryName, Integer floor);

    boolean existsByDormitory_NameAndNumber(String dormitoryName, String number);
}
