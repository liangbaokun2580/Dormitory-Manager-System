package com.dormitory.dto;

public class RoomDto {
    private Long id;
    private String dormitory;
    private Integer floor;
    private String number;
    private Integer capacity;
    private Integer occupied;
    private String status;

    public RoomDto() {}

    public RoomDto(Long id, String dormitory, Integer floor, String number, Integer capacity, Integer occupied, String status) {
        this.id = id;
        this.dormitory = dormitory;
        this.floor = floor;
        this.number = number;
        this.capacity = capacity;
        this.occupied = occupied;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDormitory() { return dormitory; }
    public void setDormitory(String dormitory) { this.dormitory = dormitory; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getOccupied() { return occupied; }
    public void setOccupied(Integer occupied) { this.occupied = occupied; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
