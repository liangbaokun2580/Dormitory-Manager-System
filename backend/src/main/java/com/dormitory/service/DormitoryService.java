package com.dormitory.service;

import com.dormitory.dto.DormitoryDto;
import com.dormitory.entity.Dormitory;
import com.dormitory.entity.Room;
import com.dormitory.entity.Student;
import com.dormitory.repository.DormitoryRepository;
import com.dormitory.repository.RoomRepository;
import com.dormitory.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DormitoryService {

    private final DormitoryRepository dormitoryRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public DormitoryService(
            DormitoryRepository dormitoryRepository,
            RoomRepository roomRepository,
            StudentRepository studentRepository
    ) {
        this.dormitoryRepository = dormitoryRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    public List<DormitoryDto> findAll() {
        return dormitoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DormitoryDto findById(Long id) {
        return dormitoryRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));
    }

    public DormitoryDto create(DormitoryDto dto) {
        Dormitory d = new Dormitory();
        d.setName(dto.getName());
        d.setFloors(dto.getFloors());
        d.setTotalRooms(dto.getTotalRooms());
        d.setCapacity(dto.getCapacity());
        d.setType(dto.getType());
        d = dormitoryRepository.save(d);
        return toDto(d);
    }

    public DormitoryDto update(Long id, DormitoryDto dto) {
        Dormitory d = dormitoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));
        d.setName(dto.getName());
        d.setFloors(dto.getFloors());
        d.setTotalRooms(dto.getTotalRooms());
        d.setCapacity(dto.getCapacity());
        d.setType(dto.getType());
        d = dormitoryRepository.save(d);
        return toDto(d);
    }

    @Transactional
    public void delete(Long id) {
        Dormitory dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));

        List<Room> rooms = roomRepository.findByDormitory_Id(id);
        for (Room room : rooms) {
            List<Student> students = studentRepository.findByRoom_Id(room.getId());
            for (Student student : students) {
                student.setRoom(null);
                studentRepository.save(student);
            }
        }

        if (!rooms.isEmpty()) {
            roomRepository.deleteAll(rooms);
        }
        dormitoryRepository.delete(dormitory);
    }

    private DormitoryDto toDto(Dormitory d) {
        return new DormitoryDto(d.getId(), d.getName(), d.getFloors(), d.getTotalRooms(), d.getCapacity(), d.getType());
    }
}
