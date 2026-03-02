package com.dormitory.service;

import com.dormitory.dto.RepairDto;
import com.dormitory.dto.StudentRepairCreateRequest;
import com.dormitory.entity.Repair;
import com.dormitory.entity.Student;
import com.dormitory.repository.RepairRepository;
import com.dormitory.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class RepairService {

    private final RepairRepository repairRepository;
    private final StudentRepository studentRepository;

    public RepairService(RepairRepository repairRepository, StudentRepository studentRepository) {
        this.repairRepository = repairRepository;
        this.studentRepository = studentRepository;
    }

    public List<RepairDto> findAll(String status, String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return repairRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(r -> status == null || status.isBlank() || status.equals(r.getStatus()))
                .filter(r -> q.isBlank() || containsIgnoreCase(r.getTitle(), q)
                        || containsIgnoreCase(r.getDormitory(), q)
                        || containsIgnoreCase(r.getRoomNumber(), q)
                        || containsIgnoreCase(r.getReporter(), q))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RepairDto> findForWorker(String status, String keyword) {
        return findAll(status, keyword);
    }

    public List<RepairDto> findMine(String studentId, String status) {
        if (studentId == null || studentId.isBlank()) {
            throw new RuntimeException("学生学号不能为空");
        }
        return repairRepository.findByStudentIdOrderByCreatedAtDesc(studentId.trim()).stream()
                .filter(r -> status == null || status.isBlank() || status.equals(r.getStatus()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RepairDto createForStudent(StudentRepairCreateRequest req) {
        if (req.getStudentId() == null || req.getStudentId().isBlank()) {
            throw new RuntimeException("学号不能为空");
        }
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new RuntimeException("报修标题不能为空");
        }
        Student student = studentRepository.findByStudentId(req.getStudentId().trim())
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        if (student.getRoom() == null) {
            throw new RuntimeException("当前未分配宿舍，无法提交报修");
        }
        LocalDateTime now = LocalDateTime.now();
        Repair repair = new Repair();
        repair.setStudentId(student.getStudentId());
        repair.setReporter(student.getName());
        repair.setDormitory(student.getRoom().getDormitory().getName());
        repair.setRoomNumber(student.getRoom().getNumber());
        repair.setTitle(req.getTitle().trim());
        repair.setDescription(req.getDescription());
        repair.setStatus("待处理");
        repair.setCreatedAt(now);
        repair.setUpdatedAt(now);
        return toDto(repairRepository.save(repair));
    }

    public RepairDto findById(Long id) {
        return repairRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("维修工单不存在"));
    }

    public RepairDto create(RepairDto dto) {
        validateTitle(dto);
        validateLocation(dto);
        LocalDateTime now = LocalDateTime.now();
        Repair repair = new Repair();
        repair.setDormitory(dto.getDormitory().trim());
        repair.setRoomNumber(dto.getRoomNumber().trim());
        repair.setReporter(dto.getReporter());
        repair.setStudentId(dto.getStudentId());
        repair.setTitle(dto.getTitle().trim());
        repair.setDescription(dto.getDescription());
        repair.setStatus(normalizeStatus(dto.getStatus()));
        repair.setCreatedAt(now);
        repair.setUpdatedAt(now);
        if ("已完成".equals(repair.getStatus())) {
            repair.setCompletedAt(now);
        }
        return toDto(repairRepository.save(repair));
    }

    public RepairDto update(Long id, RepairDto dto) {
        validateLocation(dto);
        validateTitle(dto);
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修工单不存在"));
        repair.setDormitory(dto.getDormitory().trim());
        repair.setRoomNumber(dto.getRoomNumber().trim());
        repair.setTitle(dto.getTitle().trim());
        repair.setDescription(dto.getDescription());
        repair.setReporter(dto.getReporter());
        repair.setStudentId(dto.getStudentId());
        String nextStatus = normalizeStatus(dto.getStatus());
        repair.setStatus(nextStatus);
        repair.setUpdatedAt(LocalDateTime.now());
        if ("已完成".equals(nextStatus) && repair.getCompletedAt() == null) {
            repair.setCompletedAt(LocalDateTime.now());
        }
        if (!"已完成".equals(nextStatus)) {
            repair.setCompletedAt(null);
        }
        return toDto(repairRepository.save(repair));
    }

    public RepairDto complete(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修工单不存在"));
        repair.setStatus("已完成");
        repair.setUpdatedAt(LocalDateTime.now());
        if (repair.getCompletedAt() == null) {
            repair.setCompletedAt(LocalDateTime.now());
        }
        return toDto(repairRepository.save(repair));
    }

    public RepairDto acceptByWorker(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修工单不存在"));
        if ("已完成".equals(repair.getStatus())) {
            throw new RuntimeException("该工单已完成");
        }
        repair.setStatus("处理中");
        repair.setUpdatedAt(LocalDateTime.now());
        return toDto(repairRepository.save(repair));
    }

    public RepairDto completeByWorker(Long id) {
        return complete(id);
    }

    public void delete(Long id) {
        repairRepository.deleteById(id);
    }

    private void validateLocation(RepairDto dto) {
        if (dto.getDormitory() == null || dto.getDormitory().isBlank()) {
            throw new RuntimeException("楼栋不能为空");
        }
        if (dto.getRoomNumber() == null || dto.getRoomNumber().isBlank()) {
            throw new RuntimeException("房间号不能为空");
        }
    }

    private void validateTitle(RepairDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("报修标题不能为空");
        }
    }

    private String normalizeStatus(String status) {
        if ("处理中".equals(status) || "已完成".equals(status)) return status;
        return "待处理";
    }

    private boolean containsIgnoreCase(String source, String keywordLower) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keywordLower);
    }

    private RepairDto toDto(Repair repair) {
        return new RepairDto(
                repair.getId(),
                repair.getDormitory(),
                repair.getRoomNumber(),
                repair.getTitle(),
                repair.getDescription(),
                repair.getStatus(),
                repair.getReporter(),
                repair.getStudentId(),
                repair.getCreatedAt(),
                repair.getUpdatedAt(),
                repair.getCompletedAt()
        );
    }
}
