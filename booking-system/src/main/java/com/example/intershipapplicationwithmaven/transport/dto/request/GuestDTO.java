package com.example.intershipapplicationwithmaven.transport.dto.request;
import java.io.Serializable;

public class GuestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private String passportNumber;
    private String address;

    public GuestDTO() {
    }

    public GuestDTO(String name, int age, String passportNumber, String address) {
        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
        this.address = address;
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

    public String toString() {
        return "GuestEntity{" +
                "id=" + serialVersionUID +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", passportNumber='" + passportNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
