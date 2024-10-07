package com.example.intershipapplicationwithmaven.controller;

import com.example.intershipapplicationwithmaven.repository.entity.BookingEntity;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.repository.impl.BookingRepository;
import com.example.intershipapplicationwithmaven.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("bookings")
public class BookingController {
    BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public BookingController(BookingService bookingService, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/getAllGuests")
    public ResponseEntity<List<GuestEntity>> getAllGuests(){
        return ResponseEntity.ok(bookingService.getAllGuests());
    }

    @GetMapping("/getGuest/{id}")
    public ResponseEntity<GuestEntity> getGuest(@PathVariable int id){
        return ResponseEntity.ok(bookingService.getGuestByID(id));
    }

    @PostMapping("/addGuest")
    public ResponseEntity<Void> addGuest(@RequestBody GuestEntity guestEntity) throws IOException {
        System.out.println("Received GuestEntity: " + guestEntity); // Логирование полученного объекта
        bookingService.addGuest(guestEntity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateGuest/{id}")
    public ResponseEntity<Void> updateGuest(@PathVariable int id, @RequestBody GuestEntity guestEntity){
        bookingService.updateGuestHttp(id, guestEntity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteGuest/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable int id){
        bookingService.deleteGuestHttp(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkIn")
    public ResponseEntity<String> checkIn(@RequestBody BookingEntity request) {
        int guestId = request.getGuestId();
        int hotelId = request.getHotelId();

        // Проверяем, существует ли гость
        if (!bookingService.validateCheckInGuest(guestId)) {
            return ResponseEntity.badRequest().body("Гостя с таким ID не существует.");
        }

        // Проверяем, что гость еще не заселен
        if (!bookingService.validateBookingDoubleCheckIn(guestId)) {
            return ResponseEntity.badRequest().body("Гость уже заселен в другой отель.");
        }

        // Делаем HTTP-запрос в HotelSystem для проверки доступности номера
        String availabilityUrl = "http://localhost:8081/hotels/checkAvailability?hotelId=" + hotelId;
        ResponseEntity<String> availabilityResponse = restTemplate.postForEntity(availabilityUrl, null, String.class);

        if (availabilityResponse.getBody().equals("AVAILABLE")) {
            // Заселяем гостя
            bookingService.bookRoom(guestId, hotelId);
            return ResponseEntity.ok("Гость " + guestId + " успешно заселен в отель " + hotelId);
        } else if (availabilityResponse.getBody().equals("UNAVAILABLE")) {
            return ResponseEntity.badRequest().body("Отель недоступен.");
        } else {
            return ResponseEntity.badRequest().body("В отеле нет свободных мест.");
        }
    }



}
