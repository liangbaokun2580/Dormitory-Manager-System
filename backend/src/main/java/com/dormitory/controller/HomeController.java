package com.dormitory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
                "message", "宿舍管理系统 API",
                "docs", "接口前缀 /api",
                "login", "POST /api/auth/login",
                "frontend", "请使用前端访问 http://localhost:5173"
        ));
    }
}
