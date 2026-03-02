package com.dormitory.service;

import com.dormitory.dto.StudentDto;
import com.dormitory.entity.Room;
import com.dormitory.entity.Student;
import com.dormitory.repository.RoomRepository;
import com.dormitory.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public StudentService(StudentRepository studentRepository, RoomRepository roomRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    public List<StudentDto> findAll() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return studentRepository.findByNameContainingOrStudentIdContaining(keyword, keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> findUnassigned() {
        return studentRepository.findByRoomIsNull().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto findById(Long id) {
        return studentRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    public StudentDto findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    public StudentDto create(StudentDto dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setStudentId(dto.getStudentId());
        student.setGender(dto.getGender());
        student.setCollege(dto.getCollege());
        student.setMajor(dto.getMajor());
        student.setGrade(dto.getGrade());
        student = studentRepository.save(student);
        return toDto(student);
    }

    public StudentDto update(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        student.setName(dto.getName());
        student.setStudentId(dto.getStudentId());
        student.setGender(dto.getGender());
        student.setCollege(dto.getCollege());
        student.setMajor(dto.getMajor());
        student.setGrade(dto.getGrade());
        student = studentRepository.save(student);
        return toDto(student);
    }

    @Transactional
    public void allocate(Long studentId, Long roomId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));

        if (!matchGender(student.getGender(), room.getDormitory().getType())) {
            throw new RuntimeException("学生性别与宿舍类型不匹配");
        }

        Long oldRoomId = student.getRoom() != null ? student.getRoom().getId() : null;
        if (oldRoomId != null && oldRoomId.equals(room.getId())) {
            return;
        }

        long occupied = studentRepository.countByRoom_Id(room.getId());
        int capacity = room.getCapacity() != null ? room.getCapacity() : 0;
        if (occupied >= capacity) {
            throw new RuntimeException("房间已满");
        }

        student.setRoom(room);
        studentRepository.save(student);

        syncRoomOccupied(room.getId());
        if (oldRoomId != null) {
            syncRoomOccupied(oldRoomId);
        }
    }

    @Transactional
    public void deallocate(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Room room = student.getRoom();
        if (room != null) {
            Long roomId = room.getId();
            student.setRoom(null);
            studentRepository.save(student);
            syncRoomOccupied(roomId);
        }
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Long roomId = student.getRoom() != null ? student.getRoom().getId() : null;
        studentRepository.delete(student);
        if (roomId != null) {
            syncRoomOccupied(roomId);
        }
    }

    private void syncRoomOccupied(Long roomId) {
        if (roomId == null) return;
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) return;
        int realOccupied = (int) studentRepository.countByRoom_Id(roomId);
        room.setOccupied(realOccupied);
        roomRepository.save(room);
    }

    private boolean matchGender(String studentGender, String dormType) {
        if (studentGender == null || studentGender.isBlank()) return true;
        if (dormType == null || dormType.isBlank()) return true;
        if ("男".equals(studentGender)) return "男寝".equals(dormType);
        if ("女".equals(studentGender)) return "女寝".equals(dormType);
        return true;
    }

    private StudentDto toDto(Student s) {
        String roomStr = s.getRoom() != null
                ? s.getRoom().getDormitory().getName() + "-" + s.getRoom().getNumber()
                : null;
        return new StudentDto(s.getId(), s.getName(), s.getStudentId(), s.getGender(),
                s.getCollege(), s.getMajor(), s.getGrade(), roomStr);
    }
}
