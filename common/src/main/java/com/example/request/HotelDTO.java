package com.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class HotelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hotelName;

    // Конструкторы
    public HotelDTO() {}

    public HotelDTO( String hotelName) {
        this.hotelName = hotelName;
    }


    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
}

