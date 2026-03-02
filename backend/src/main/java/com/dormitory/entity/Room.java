package com.dormitory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id", nullable = false)
    private Dormitory dormitory;

    private Integer floor;

    @Column(nullable = false)
    private String number;

    private Integer capacity;

    private Integer occupied;

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Dormitory getDormitory() { return dormitory; }
    public void setDormitory(Dormitory dormitory) { this.dormitory = dormitory; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getOccupied() { return occupied; }
    public void setOccupied(Integer occupied) { this.occupied = occupied; }

    @Transient
    public String getStatus() {
        int occ = occupied != null ? occupied : 0;
        int cap = capacity != null ? capacity : 0;
        if (occ == 0) return "空";
        if (occ >= cap) return "已满";
        return "部分入住";
    }
}
