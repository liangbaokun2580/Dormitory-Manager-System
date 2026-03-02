package com.dormitory.controller;

import com.dormitory.dto.RepairDto;
import com.dormitory.service.RepairService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping
    public List<RepairDto> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return repairService.findAll(status, keyword);
    }

    @GetMapping("/{id}")
    public RepairDto get(@PathVariable Long id) {
        return repairService.findById(id);
    }

    @PostMapping
    public RepairDto create(@RequestBody RepairDto dto) {
        return repairService.create(dto);
    }

    @PutMapping("/{id}")
    public RepairDto update(@PathVariable Long id, @RequestBody RepairDto dto) {
        return repairService.update(id, dto);
    }

    @PostMapping("/{id}/complete")
    public RepairDto complete(@PathVariable Long id) {
        return repairService.complete(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repairService.delete(id);
    }
}
