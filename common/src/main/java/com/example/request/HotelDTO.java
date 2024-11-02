package com.example.request;


import java.io.Serializable;

public class HotelDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String hotelName;
    private Integer availability;

    public HotelDTO() {}

    public HotelDTO(int id, String hotelName, Integer availability) {
        this.id = id;
        this.hotelName = hotelName;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }
}

