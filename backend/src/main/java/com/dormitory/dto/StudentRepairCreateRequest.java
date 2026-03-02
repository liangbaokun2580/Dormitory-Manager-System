package com.dormitory.dto;

public class StudentRepairCreateRequest {
    private String studentId;
    private String title;
    private String description;

    public StudentRepairCreateRequest() {}

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
