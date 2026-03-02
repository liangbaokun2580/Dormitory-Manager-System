package com.dormitory.service;

import com.dormitory.dto.RoomDto;
import com.dormitory.entity.Dormitory;
import com.dormitory.entity.Room;
import com.dormitory.repository.DormitoryRepository;
import com.dormitory.repository.RoomRepository;
import com.dormitory.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final DormitoryRepository dormitoryRepository;
    private final StudentRepository studentRepository;

    public RoomService(RoomRepository roomRepository, DormitoryRepository dormitoryRepository, StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.dormitoryRepository = dormitoryRepository;
        this.studentRepository = studentRepository;
    }

    public List<RoomDto> findAll(String dormitoryName, String status) {
        List<Room> rooms = dormitoryName != null && !dormitoryName.isBlank()
                ? roomRepository.findByDormitory_Name(dormitoryName)
                : roomRepository.findAll();

        return rooms.stream()
                .map(this::syncOccupied)
                .map(this::toDto)
                .filter(dto -> status == null || status.isBlank() || status.equals(dto.getStatus()))
                .collect(Collectors.toList());
    }

    public List<RoomDto> findAvailable(String gender) {
        return roomRepository.findAll().stream()
                .map(this::syncOccupied)
                .filter(r -> matchGender(r.getDormitory().getType(), gender))
                .filter(r -> (r.getOccupied() != null ? r.getOccupied() : 0) < (r.getCapacity() != null ? r.getCapacity() : 0))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto findById(Long id) {
        return roomRepository.findById(id)
                .map(this::syncOccupied)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
    }

    public RoomDto create(RoomDto dto) {
        if (dto.getDormitory() == null || dto.getDormitory().isBlank()) {
            throw new RuntimeException("楼栋不能为空");
        }
        if (dto.getNumber() == null || dto.getNumber().isBlank()) {
            throw new RuntimeException("房间号不能为空");
        }
        if (roomRepository.existsByDormitory_NameAndNumber(dto.getDormitory().trim(), dto.getNumber().trim())) {
            throw new RuntimeException("该楼栋下房间号已存在");
        }

        Dormitory dormitory = dormitoryRepository.findByName(dto.getDormitory().trim())
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));

        Room room = new Room();
        room.setDormitory(dormitory);
        room.setFloor(dto.getFloor());
        room.setNumber(dto.getNumber().trim());
        room.setCapacity(dto.getCapacity() == null ? 4 : dto.getCapacity());
        room.setOccupied(dto.getOccupied() == null ? 0 : dto.getOccupied());

        if (room.getCapacity() < 0 || room.getOccupied() < 0 || room.getOccupied() > room.getCapacity()) {
            throw new RuntimeException("房间容量或已住人数不合法");
        }

        room = roomRepository.save(room);
        return toDto(room);
    }

    private Room syncOccupied(Room room) {
        int realOccupied = (int) studentRepository.countByRoom_Id(room.getId());
        if (room.getOccupied() == null || room.getOccupied() != realOccupied) {
            room.setOccupied(realOccupied);
            return roomRepository.save(room);
        }
        return room;
    }

    private boolean matchGender(String dormType, String studentGender) {
        if (studentGender == null || studentGender.isBlank()) return true;
        if (dormType == null || dormType.isBlank()) return true;
        if ("男".equals(studentGender)) return "男寝".equals(dormType);
        if ("女".equals(studentGender)) return "女寝".equals(dormType);
        return true;
    }

    private RoomDto toDto(Room r) {
        return new RoomDto(r.getId(), r.getDormitory().getName(), r.getFloor(), r.getNumber(),
                r.getCapacity(), r.getOccupied(), r.getStatus());
    }
}
