package com.campus.campusbookingsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer capacity;

    private String building;

    @Column(length = 1000)
    private String equipment;

    @Column(nullable = false)
    private Boolean active = true;


    public Room() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Integer getCapacity() {
        return capacity;
    }


    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


    public String getBuilding() {
        return building;
    }


    public void setBuilding(String building) {
        this.building = building;
    }


    public String getEquipment() {
        return equipment;
    }


    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}