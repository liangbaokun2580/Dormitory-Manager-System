package com.dormitory.dto;

public class WorkerLoginResponse {
    private String token;
    private String username;

    public WorkerLoginResponse() {}

    public WorkerLoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
