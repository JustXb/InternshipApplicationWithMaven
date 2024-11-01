package com.example.hotelsystem;

import com.example.hotelsystem.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelSystemApplication implements CommandLineRunner {


    @Autowired
    public HotelSystemApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(HotelSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
