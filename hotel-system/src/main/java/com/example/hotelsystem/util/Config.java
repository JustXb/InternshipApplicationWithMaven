package com.example.hotelsystem.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${processor.hotels.json.filepath}")
    private String hotelsPath;

    @Value("${processor.hotelsAvailability.json.filepath}")
    private String hotelsAvailabilityPath;

    @Value("${processor.port}")
    private int port;

    public String getHotelsPath() {
        return hotelsPath;
    }

    public String getHotelsAvailabilityPath() {
        return hotelsAvailabilityPath;
    }

    public int getPort() {
        return port;
    }
}
