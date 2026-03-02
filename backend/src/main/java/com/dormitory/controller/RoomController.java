package com.dormitory.controller;

import com.dormitory.dto.RoomDto;
import com.dormitory.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomDto> list(
            @RequestParam(required = false) String dormitory,
            @RequestParam(required = false) String status) {
        return roomService.findAll(dormitory, status);
    }

    @GetMapping("/available")
    public List<RoomDto> available(@RequestParam(required = false) String gender) {
        return roomService.findAvailable(gender);
    }

    @GetMapping("/{id}")
    public RoomDto get(@PathVariable Long id) {
        return roomService.findById(id);
    }

    @PostMapping
    public RoomDto create(@RequestBody RoomDto dto) {
        return roomService.create(dto);
    }
}
