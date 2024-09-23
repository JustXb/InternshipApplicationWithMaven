package com.example.hotelsystem.util;
import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;
import com.example.hotelsystem.transport.dto.request.HotelDTO;
import com.example.hotelsystem.transport.dto.request.HotelAvailabilityDTO;

public class Mapper {

    public static HotelDTO toHotelDTO(HotelEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HotelDTO(
                entity.getHotelName()
        );
    }

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

    public static HotelAvailablilityEntity toHotelEntity(HotelAvailabilityDTO dto) {
        if (dto == null) {
            return null;
        }
        HotelAvailablilityEntity entity = new HotelAvailablilityEntity();
        entity.setId(dto.getId());
        entity.setAvailability(dto.getAvailableRooms());
        return entity;
    }


}
