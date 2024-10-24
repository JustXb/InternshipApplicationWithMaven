package com.example.intershipapplicationwithmaven.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@javax.persistence.Entity
@Table(name = "guests")
public class GuestEntity extends Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int age;
    @Column(nullable = false)
    private String passportNumber;
    @Column(nullable = false)
    private String address;

    @OneToOne(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true) // Связь с таблицей бронирований
    @JsonManagedReference // Указываем, что это управляемая ссылка
    private BookingEntity booking;

    public GuestEntity(int id, String name, int age, String passportNumber, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
        this.address = address;
    }

    public GuestEntity(String name, int age, String passportNumber, String address) {
        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
        this.address = address;
    }

    public GuestEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BookingEntity getBooking() {
        return booking;
    }

    public void getInfo() {
        System.out.println("ID : " + this.id);
        System.out.println("Name : " + this.name);
        System.out.println("Age : " + this.age);
        System.out.println("Passport Number : " + this.passportNumber);
        System.out.println("Address : " + this.address + '\n');
    }

    @Override
    public String toString() {
        return "GuestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", passportNumber='" + passportNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
