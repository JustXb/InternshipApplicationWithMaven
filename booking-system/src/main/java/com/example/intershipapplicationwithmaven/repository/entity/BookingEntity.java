package com.example.intershipapplicationwithmaven.repository.entity;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "bookings")
public class BookingEntity extends Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int guestId;

    @Column(nullable = false)
    private int hotelId;

    // Constructors, Getters, and Setters

    public BookingEntity(int id, int guestId, int hotelId) {
        this.id = id;
        this.guestId = guestId;
        this.hotelId = hotelId;
    }

    public BookingEntity(int guestId, int hotelId) {
        this.guestId = guestId;
        this.hotelId = hotelId;
    }

    public BookingEntity() {
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

    @Override
    public String toString() {
        return "BookingEntity{" +
                "id=" + id +
                ", guestId=" + guestId +
                ", hotelId=" + hotelId +
                '}';
    }
}
