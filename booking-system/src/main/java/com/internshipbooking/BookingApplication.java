package com.internshipbooking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingApplication implements CommandLineRunner {

    public BookingApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }



    @Override
    public void run(String... args) throws Exception {
        System.out.println("Введите help для получения помощи");
    }
}

