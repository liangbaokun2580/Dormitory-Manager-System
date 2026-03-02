package com.dormitory.dto;

public class StudentDto {
    private Long id;
    private String name;
    private String studentId;
    private String gender;
    private String college;
    private String major;
    private String grade;
    private String room;

    public StudentDto() {}

    public StudentDto(Long id, String name, String studentId, String gender, String college, String major, String grade, String room) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.gender = gender;
        this.college = college;
        this.major = major;
        this.grade = grade;
        this.room = room;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}
