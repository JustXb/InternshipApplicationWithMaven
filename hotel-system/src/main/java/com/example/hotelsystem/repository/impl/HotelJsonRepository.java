package com.example.hotelsystem.repository.impl;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Repository
public class HotelJsonRepository {

    private final String hotelsFilePath;

    public HotelJsonRepository(@Value("${processor.hotels.json.filepath}") String hotelsFilePath) {
        this.hotelsFilePath = hotelsFilePath;
    }

    public List<HotelEntity> loadHotelsFromFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(hotelsFilePath), new TypeReference<List<HotelEntity>>() {});
    }

    public HotelAvailablilityEntity readHotelFromFile(int id) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("HotelsAvailability.json");

        List<HotelAvailablilityEntity> hotels = objectMapper.readValue(file, new TypeReference<List<HotelAvailablilityEntity>>() {});
        for (HotelAvailablilityEntity hotel : hotels) {
            if (hotel.getId() == id) {
                return hotel; // Возвращаем найденный отель
            }
        }
        return null;
    }
}
