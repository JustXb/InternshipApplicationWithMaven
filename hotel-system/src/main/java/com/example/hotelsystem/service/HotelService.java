package com.example.hotelsystem.service;

import com.example.hotelsystem.mapper.Mapper;
import com.example.hotelsystem.repository.impl.HotelAvailabilityRepository;
import com.example.hotelsystem.repository.impl.HotelRepository;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;


import com.example.request.AvailabilityDto;
import com.example.request.HotelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelAvailabilityRepository hotelAvailabilityRepository;
    private final Mapper mapper;

    private final Logger LOGGER = Logger.getLogger(HotelService.class.getName());

    @Autowired
    public HotelService(HotelAvailabilityRepository hotelAvailabilityRepository, HotelRepository hotelRepository, Mapper mapper) {
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
        this.hotelRepository = hotelRepository;
        this.mapper = mapper;
    }


    public boolean isHotelAvailable(int id) {
        Optional<HotelEntity> hotelOpt = hotelRepository.findById(id);

        if (hotelOpt.isEmpty()) {
            LOGGER.warning(ServiceMessages.WRONG_HOTEL.getMessage());
            return true;
        }

        Optional<HotelAvailablilityEntity> hotelAvailabilityOpt = hotelAvailabilityRepository.findById(id);

        if (hotelAvailabilityOpt.isEmpty()) {
            AvailabilityDto availabilityDto = new AvailabilityDto();
            availabilityDto.setAvailable(false);
            availabilityDto.setReason(ServiceMessages.UNAVAILABLE.getMessage());
            LOGGER.warning(ServiceMessages.UNAVAILABLE.getMessage());
            return true;
        }

        return false;
    }

    public String checkHotelAvailability(int hotelId) {
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (hotelAvailability.isEmpty()) {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }

        HotelAvailablilityEntity availability = hotelAvailability.get();
        if (!availability.decreaseAvailableRooms()) {
            return ServiceMessages.LACK_OF_PLACES.getMessage();
        }

        hotelAvailabilityRepository.save(availability);
        return ServiceMessages.AVAILABLE.getMessage();
    }

    public String increaseAvailability(int hotelId){
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (hotelAvailability.isEmpty()) {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }
        HotelAvailablilityEntity availability = hotelAvailability.get();
        availability.setAvailability(availability.getAvailability() + 1);
        hotelAvailabilityRepository.save(availability);
        return ServiceMessages.INCREASE_HOTEL_AVAILABILITY.getMessage();
    }

    public String checkRoomAvailability(int hotelId) {
        if (!isHotelAvailable(hotelId)) {
            return ServiceMessages.AVAILABLE.getMessage();
        } else {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }
    }


    public List<HotelDTO> getAllHotels() {
        List<HotelDTO> hotelDTOs = new ArrayList<>();
        List<HotelEntity> hotels = hotelRepository.findAll();


        for (HotelEntity hotel : hotels) {
            Optional<HotelAvailablilityEntity> availabilityEntity = hotelAvailabilityRepository.findById(hotel.getId());
            HotelDTO hotelDTO = mapper.toHotelDto(hotel, availabilityEntity.get());
            hotelDTOs.add(hotelDTO);
        }
        return hotelDTOs;
    }


}
