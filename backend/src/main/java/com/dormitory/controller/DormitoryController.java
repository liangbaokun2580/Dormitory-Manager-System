package com.dormitory.controller;

import com.dormitory.dto.DormitoryDto;
import com.dormitory.service.DormitoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dormitories")
public class DormitoryController {

    private final DormitoryService dormitoryService;

    public DormitoryController(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    @GetMapping
    public List<DormitoryDto> list() {
        return dormitoryService.findAll();
    }

    @GetMapping("/{id}")
    public DormitoryDto get(@PathVariable Long id) {
        return dormitoryService.findById(id);
    }

    @PostMapping
    public DormitoryDto create(@RequestBody DormitoryDto dto) {
        return dormitoryService.create(dto);
    }

    @PutMapping("/{id}")
    public DormitoryDto update(@PathVariable Long id, @RequestBody DormitoryDto dto) {
        return dormitoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dormitoryService.delete(id);
    }
}
