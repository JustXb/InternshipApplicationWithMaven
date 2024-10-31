package com.example.hotelsystem.controller;

import com.example.hotelsystem.repository.entity.HotelEntity;
import com.example.hotelsystem.repository.impl.HotelAvailabilityRepository;
import com.example.hotelsystem.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/availability")
    public ResponseEntity<String> checkRoomAvailability(@PathVariable int hotelId) {
        return ResponseEntity.ok(hotelService.checkRoomAvailability(hotelId));
    }

    @PostMapping("/checkAvailability")
    public ResponseEntity<String> checkHotelAvailability(@RequestParam int hotelId) {
        return ResponseEntity.ok(hotelService.checkHotelAvailability(hotelId));
    }

    @PostMapping("/increaseAvailability")
    public ResponseEntity<String> increaseAvailability(@RequestParam int hotelId){
        return ResponseEntity.ok(hotelService.increaseAvailability(hotelId));
    }
}
