package com.example.hotelsystem.transport.dto.request;

import java.io.Serializable;

public class HotelAvailabilityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int availableRooms;

    // Конструкторы
    public HotelAvailabilityDTO() {}

    public HotelAvailabilityDTO(int id, int availableRooms) {
        this.id = id;
        this.availableRooms = availableRooms;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }
}
