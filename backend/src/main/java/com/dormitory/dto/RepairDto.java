package com.dormitory.dto;

import java.time.LocalDateTime;

public class RepairDto {
    private Long id;
    private String dormitory;
    private String roomNumber;
    private String title;
    private String description;
    private String status;
    private String reporter;
    private String studentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public RepairDto() {}

    public RepairDto(Long id, String dormitory, String roomNumber, String title, String description,
                     String status, String reporter, String studentId, LocalDateTime createdAt, LocalDateTime updatedAt,
                     LocalDateTime completedAt) {
        this.id = id;
        this.dormitory = dormitory;
        this.roomNumber = roomNumber;
        this.title = title;
        this.description = description;
        this.status = status;
        this.reporter = reporter;
        this.studentId = studentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDormitory() { return dormitory; }
    public void setDormitory(String dormitory) { this.dormitory = dormitory; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReporter() { return reporter; }
    public void setReporter(String reporter) { this.reporter = reporter; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
