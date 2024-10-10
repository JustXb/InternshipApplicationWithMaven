package com.example.hotelsystem.controller;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;
import com.example.hotelsystem.repository.impl.HotelAvailabilityRepository;
import com.example.hotelsystem.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("hotels")
public class HotelController {

    HotelService hotelService;
    HotelAvailabilityRepository hotelAvailabilityRepository;

    public HotelController(HotelService hotelService, HotelAvailabilityRepository hotelAvailabilityRepository) {
        this.hotelService = hotelService;
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
    }

    @GetMapping("/getAllHotels")
    public ResponseEntity<List<HotelEntity>> getAllHotels() {
        List<HotelEntity> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{hotelId}/availability")
    public ResponseEntity<String> checkRoomAvailability(@PathVariable int hotelId) {
        if (!hotelService.isHotelAvailable(hotelId)) {
            return ResponseEntity.ok("AVAILABLE");
        } else {
            return ResponseEntity.ok("UNAVAILABLE");
        }
    }

    @PostMapping("/checkAvailability")
    public ResponseEntity<String> checkHotelAvailability(@RequestParam int hotelId) {
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (!hotelAvailability.isPresent()) {
            return ResponseEntity.ok("UNAVAILABLE");
        }

        HotelAvailablilityEntity availability = hotelAvailability.get();
        if (!availability.decreaseAvailableRooms()) {
            return ResponseEntity.ok("UNAVAILABLE_NOAVAILABILITY");
        }

        hotelAvailabilityRepository.save(availability);
        return ResponseEntity.ok("AVAILABLE");
    }

    @PostMapping("/increaseAvailability")
    public ResponseEntity<String> increaseAvailability(@RequestParam int hotelId){
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (!hotelAvailability.isPresent()) {
            return ResponseEntity.ok("UNAVAILABLE");
        }
        HotelAvailablilityEntity availability = hotelAvailability.get();
        availability.setAvailability(availability.getAvailability()+1);
        hotelAvailabilityRepository.save(availability);
        return ResponseEntity.ok("Доступность мест в отеле увеличена на 1");
    }
}
