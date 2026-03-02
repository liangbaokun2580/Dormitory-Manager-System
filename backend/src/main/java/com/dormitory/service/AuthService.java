package com.dormitory.service;

import com.dormitory.dto.LoginRequest;
import com.dormitory.dto.LoginResponse;
import com.dormitory.dto.StudentDto;
import com.dormitory.dto.StudentLoginRequest;
import com.dormitory.dto.StudentLoginResponse;
import com.dormitory.dto.WorkerLoginRequest;
import com.dormitory.dto.WorkerLoginResponse;
import com.dormitory.entity.Student;
import com.dormitory.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
            StudentRepository studentRepository,
            @Value("${auth.admin.username:admin}") String adminUsername,
            @Value("${auth.admin.password:admin123}") String adminPassword
    ) {
        this.studentRepository = studentRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public LoginResponse login(LoginRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()
                || req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("账号和密码不能为空");
        }
        String username = req.getUsername().trim();
        String password = req.getPassword();
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            throw new RuntimeException("管理员账号或密码错误");
        }
        String token = "Bearer-" + UUID.randomUUID().toString();
        return new LoginResponse(token, username);
    }

    public WorkerLoginResponse workerLogin(WorkerLoginRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()
                || req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("账号和密码不能为空");
        }
        if (!"worker".equals(req.getUsername().trim()) || !"123456".equals(req.getPassword())) {
            throw new RuntimeException("维修账号或密码错误");
        }
        String token = "Worker-" + UUID.randomUUID();
        return new WorkerLoginResponse(token, "维修师傅");
    }

    @Transactional(readOnly = true)
    public StudentLoginResponse studentLogin(StudentLoginRequest req) {
        if (req.getStudentId() == null || req.getStudentId().isBlank()
                || req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("学号和姓名不能为空");
        }
        Student student = studentRepository.findByStudentId(req.getStudentId().trim())
                .orElseThrow(() -> new RuntimeException("学号不存在"));
        if (!student.getName().equals(req.getName().trim())) {
            throw new RuntimeException("学号与姓名不匹配");
        }
        String token = "Student-" + UUID.randomUUID();
        return new StudentLoginResponse(token, toDto(student));
    }

    private StudentDto toDto(Student s) {
        String roomStr = s.getRoom() != null
                ? s.getRoom().getDormitory().getName() + "-" + s.getRoom().getNumber()
                : null;
        return new StudentDto(s.getId(), s.getName(), s.getStudentId(), s.getGender(),
                s.getCollege(), s.getMajor(), s.getGrade(), roomStr);
    }
}
