package com.example.intershipapplicationwithmaven.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
    @Value("${processor.port}")
    private int port;

    @Value("${receiver.bookings.csv.filepath}")
    private String bookingsPath;

    @Value("${receiver.guests.csv.filepath}")
    private String guestsPath;




    public int getPort() {
        return port;
    }

    public String getBookingsPath() {
        return bookingsPath;
    }

    public String getGuestsPath() {
        return guestsPath;
    }
}

