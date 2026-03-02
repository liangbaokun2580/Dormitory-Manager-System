package com.dormitory.controller;

import com.dormitory.dto.LoginRequest;
import com.dormitory.dto.LoginResponse;
import com.dormitory.dto.StudentLoginRequest;
import com.dormitory.dto.StudentLoginResponse;
import com.dormitory.dto.WorkerLoginRequest;
import com.dormitory.dto.WorkerLoginResponse;
import com.dormitory.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/student-login")
    public StudentLoginResponse studentLogin(@RequestBody StudentLoginRequest request) {
        return authService.studentLogin(request);
    }

    @PostMapping("/worker-login")
    public WorkerLoginResponse workerLogin(@RequestBody WorkerLoginRequest request) {
        return authService.workerLogin(request);
    }
}
