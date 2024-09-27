package com.example.intershipapplicationwithmaven.repository.entity;

public class BookingEntity extends Entity {
    private int id;
    private int guestId;
    private int hotelId;

    public BookingEntity(int id, int guestId, int hotelId) {
        this.guestId = guestId;
        this.hotelId = hotelId;
    }

    public BookingEntity(int guestId, int hotelId) {
        this.guestId = guestId;
        this.hotelId = hotelId;
    }

    public BookingEntity(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public void getInfo() {
        System.out.println("ID : " + this.id);
        System.out.println("GuestID : " + this.guestId);
        System.out.println("HotelID : " + this.hotelId +'\n');

    }
}
