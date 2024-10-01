package com.example.hotelsystem.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotels")
public class HotelEntity {
    @Id
    private int id;
    private String hotelName;


    public HotelEntity() {
    }

    public HotelEntity(int id, String hotelName) {
        this.id = id;
        this.hotelName = hotelName;

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



    public void getInfo() {
        System.out.println("ID : " + this.id);
        System.out.println("Hotel name : " + this.hotelName);


    }

    @Override
    public String toString() {
            return id + "," + hotelName ;

    }
}
