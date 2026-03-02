package com.dormitory.controller;

import com.dormitory.dto.AllocationRequest;
import com.dormitory.dto.StudentDto;
import com.dormitory.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentDto> list(@RequestParam(required = false) String keyword) {
        return studentService.search(keyword);
    }

    @GetMapping("/unassigned")
    public List<StudentDto> unassigned() {
        return studentService.findUnassigned();
    }

    @GetMapping("/{id}")
    public StudentDto get(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @GetMapping("/by-student-id/{studentId}")
    public StudentDto getByStudentId(@PathVariable String studentId) {
        return studentService.findByStudentId(studentId);
    }

    @PostMapping
    public StudentDto create(@RequestBody StudentDto dto) {
        return studentService.create(dto);
    }

    @PutMapping("/{id}")
    public StudentDto update(@PathVariable Long id, @RequestBody StudentDto dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }

    @PostMapping("/allocate")
    public void allocate(@RequestBody AllocationRequest request) {
        studentService.allocate(request.getStudentId(), request.getRoomId());
    }

    @PostMapping("/{id}/deallocate")
    public void deallocate(@PathVariable Long id) {
        studentService.deallocate(id);
    }
}
