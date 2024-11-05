package com.example.hotelsystem.controller;

import com.example.hotelsystem.service.HotelService;
import com.example.request.HotelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotels")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/getAllHotels")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{hotelId}/availability")
    public ResponseEntity<String> checkRoomAvailability(@PathVariable int hotelId) {
        String availability = hotelService.checkRoomAvailability(hotelId);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/checkAvailability")
    public ResponseEntity<String> checkHotelAvailability(@RequestParam int hotelId) {
        String checkHotelAvailabilityMessage = hotelService.checkHotelAvailability(hotelId);
        return ResponseEntity.ok(checkHotelAvailabilityMessage);
    }

    @PostMapping("/increaseAvailability")
    public ResponseEntity<String> increaseAvailability(@RequestParam int hotelId){
        String increaseAvailabilityMessage = hotelService.increaseAvailability(hotelId);
        return ResponseEntity.ok(increaseAvailabilityMessage);
    }
}
