package com.example.hotelsystem.mapper;
import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;

import com.example.request.HotelAvailabilityDTO;
import com.example.request.HotelDTO;
import org.springframework.stereotype.Component;

@Component
public class Mapper {


    public static HotelEntity toHotelEntity(HotelDTO dto) {
        if (dto == null) {
            return null;
        }
        HotelEntity entity = new HotelEntity();
        entity.setHotelName(dto.getHotelName());
        return entity;
    }




    public static HotelAvailabilityDTO toHotelAvailabilityDTO(HotelAvailablilityEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HotelAvailabilityDTO(
                entity.getId(),
                entity.getAvailability()
        );
    }

    public static HotelAvailablilityEntity toHotelAvailabilityEntity(HotelAvailabilityDTO dto) {
        if (dto == null) {
            return null;
        }
        HotelAvailablilityEntity entity = new HotelAvailablilityEntity();
        entity.setId(dto.getId());
        entity.setAvailability(dto.getAvailableRooms());
        return entity;
    }


    public HotelDTO toHotelDto(HotelEntity hotel, HotelAvailablilityEntity availablilityEntity) {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(hotel.getId());
        hotelDTO.setHotelName(hotel.getHotelName());
        hotelDTO.setAvailability(availablilityEntity.getAvailability());

        return hotelDTO;
    }
}
