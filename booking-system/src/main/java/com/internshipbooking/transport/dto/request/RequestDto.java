package com.internshipbooking.transport.dto.request;

public class RequestDto {

    private int id;
    private String name;
    private int age;
    private String passportNumber;
    private String address;

    public RequestDto(String name, int age, String passportNumber, String address) {

        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
        this.address = address;
    }

    public RequestDto() {

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
}
