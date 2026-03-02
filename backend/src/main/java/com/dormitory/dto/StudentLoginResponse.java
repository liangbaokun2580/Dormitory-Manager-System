package com.dormitory.dto;

public class StudentLoginResponse {
    private String token;
    private StudentDto student;

    public StudentLoginResponse() {}

    public StudentLoginResponse(String token, StudentDto student) {
        this.token = token;
        this.student = student;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public StudentDto getStudent() { return student; }
    public void setStudent(StudentDto student) { this.student = student; }
}
