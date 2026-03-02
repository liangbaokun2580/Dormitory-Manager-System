package com.dormitory.controller;

import com.dormitory.dto.RepairDto;
import com.dormitory.service.RepairService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worker")
public class WorkerPortalController {

    private final RepairService repairService;

    public WorkerPortalController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping("/repairs")
    public List<RepairDto> repairs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return repairService.findForWorker(status, keyword);
    }

    @PostMapping("/repairs/{id}/accept")
    public RepairDto accept(@PathVariable Long id) {
        return repairService.acceptByWorker(id);
    }

    @PostMapping("/repairs/{id}/complete")
    public RepairDto complete(@PathVariable Long id) {
        return repairService.completeByWorker(id);
    }
}
