package com.internshipbooking.transport.dto.response;

public class ResponseDto {
    private static int counter = 0;

    private int id;
    private String name;
    private int age;
    private String passportNumber;
    private String address;

    public ResponseDto(String name, int age, String passportNumber, String address) {
        ++counter;
        this.id = counter;
        this.name = name;
        this.age = age;
        this.passportNumber = passportNumber;
        this.address = address;
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

    public void getInfo() {
        System.out.println("ID : " + this.id);
        System.out.println("Name : " + this.name);
        System.out.println("Age : " + this.age);
        System.out.println("Passport Number : " + this.passportNumber);
        System.out.println("Address : " + this.address + '\n');
    }
}
