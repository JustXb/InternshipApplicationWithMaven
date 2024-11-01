package com.internshipbooking.transport.dto.request;
import com.internshipbooking.service.ServiceMessages;

import java.io.Serializable;

import javax.validation.constraints.*;

public class GuestDTO implements Serializable {

    private static final long serialVersionUID = 1L;


        private int id;

        @NotBlank(message = "имя не может быть пустым.")
        @Size(max = 20, message = "Имя гостя не может быть длиннее 20 символов")
        @Pattern(regexp = "^[A-Z].*", message = "Имя гостя должно начинаться с большой буквы")
        private String name;

        @Min(value = 0, message = "Возраст должен быть больше 0")
        @Max(value = 120, message = "Возраст должен быть меньше 121")
        private int age;

        @NotBlank(message = "Адрес не может быть пустым.")
        @Size(max = 30, message = "Адрес не может быть длиннее 30 символов")
        @Pattern(regexp = "^[A-Z].*", message = "Адрес должен начинаться с большой буквы")
        private String address;

        @NotNull(message = "Номер паспорта не может быть пустым.")
        @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен содержать ровно 6 цифр.")
        private String passportNumber;


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
