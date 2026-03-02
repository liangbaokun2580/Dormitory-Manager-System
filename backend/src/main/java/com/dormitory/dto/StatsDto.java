package com.dormitory.dto;

public class StatsDto {
    private long totalStudents;
    private long totalRooms;
    private long occupiedRooms;
    private long vacancy;

    public StatsDto() {}

    public StatsDto(long totalStudents, long totalRooms, long occupiedRooms, long vacancy) {
        this.totalStudents = totalStudents;
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.vacancy = vacancy;
    }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }
    public long getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(long occupiedRooms) { this.occupiedRooms = occupiedRooms; }
    public long getVacancy() { return vacancy; }
    public void setVacancy(long vacancy) { this.vacancy = vacancy; }
}
