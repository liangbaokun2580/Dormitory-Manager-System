package com.dormitory.dto;

public class DormitoryDto {
    private Long id;
    private String name;
    private Integer floors;
    private Integer totalRooms;
    private Integer capacity;
    private String type;

    public DormitoryDto() {}

    public DormitoryDto(Long id, String name, Integer floors, Integer totalRooms, Integer capacity, String type) {
        this.id = id;
        this.name = name;
        this.floors = floors;
        this.totalRooms = totalRooms;
        this.capacity = capacity;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public Integer getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Integer totalRooms) { this.totalRooms = totalRooms; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
