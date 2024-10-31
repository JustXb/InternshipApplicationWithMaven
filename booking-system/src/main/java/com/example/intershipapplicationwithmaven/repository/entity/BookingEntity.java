package com.example.intershipapplicationwithmaven.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "guest_id", nullable = false)
    @JsonBackReference
    private GuestEntity guest;

    @Column(nullable = false)
    private int hotelId;


    public BookingEntity(int id, GuestEntity guest, int hotelId) {
        this.id = id;
        this.guest = guest;
        this.hotelId = hotelId;
    }

    public BookingEntity(GuestEntity guest, int hotelId) {
        this.guest = guest;
        this.hotelId = hotelId;
    }

    public BookingEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GuestEntity getGuest() {
        return guest;
    }

    public void setGuest(GuestEntity guest) {
        this.guest = guest;
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
                ", guestId=" + guest.getId() +
                ", hotelId=" + hotelId +
                '}';
    }
}
