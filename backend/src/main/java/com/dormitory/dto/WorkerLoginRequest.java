package com.dormitory.dto;

public class WorkerLoginRequest {
    private String username;
    private String password;

    public WorkerLoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
