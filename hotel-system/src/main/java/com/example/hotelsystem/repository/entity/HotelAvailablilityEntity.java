package com.example.hotelsystem.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "availability")
public class HotelAvailablilityEntity {
    @Id
    private int id;
    private int availability;

    public HotelAvailablilityEntity(int id, int availability) {
        this.id = id;
        this.availability = availability;
    }
    public HotelAvailablilityEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public boolean decreaseAvailableRooms() {
        if (availability > 0) {
            availability--;
            return true;
        } else {
            return false;
        }
    }
}



