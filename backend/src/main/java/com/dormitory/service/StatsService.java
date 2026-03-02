package com.dormitory.service;

import com.dormitory.dto.StatsDto;
import com.dormitory.repository.RoomRepository;
import com.dormitory.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public StatsService(StudentRepository studentRepository, RoomRepository roomRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    public StatsDto getStats() {
        long totalStudents = studentRepository.count();
        long totalRooms = roomRepository.count();
        long occupiedRooms = roomRepository.findAll().stream()
                .filter(r -> r.getOccupied() != null && r.getOccupied() > 0)
                .count();
        long totalCapacity = roomRepository.findAll().stream()
                .mapToInt(r -> r.getCapacity() != null ? r.getCapacity() : 0)
                .sum();
        long totalOccupied = roomRepository.findAll().stream()
                .mapToInt(r -> r.getOccupied() != null ? r.getOccupied() : 0)
                .sum();
        long vacancy = totalCapacity - totalOccupied;
        return new StatsDto(totalStudents, totalRooms, occupiedRooms, vacancy);
    }
}
