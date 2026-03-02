package com.dormitory.controller;

import com.dormitory.dto.RepairDto;
import com.dormitory.dto.StudentDto;
import com.dormitory.dto.StudentRepairCreateRequest;
import com.dormitory.service.RepairService;
import com.dormitory.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentPortalController {

    private final StudentService studentService;
    private final RepairService repairService;

    public StudentPortalController(StudentService studentService, RepairService repairService) {
        this.studentService = studentService;
        this.repairService = repairService;
    }

    @GetMapping("/profile/{studentId}")
    public StudentDto profile(@PathVariable String studentId) {
        return studentService.findByStudentId(studentId);
    }

    @GetMapping("/repairs/{studentId}")
    public List<RepairDto> myRepairs(
            @PathVariable String studentId,
            @RequestParam(required = false) String status
    ) {
        return repairService.findMine(studentId, status);
    }

    @PostMapping("/repairs")
    public RepairDto submitRepair(@RequestBody StudentRepairCreateRequest request) {
        return repairService.createForStudent(request);
    }
}
