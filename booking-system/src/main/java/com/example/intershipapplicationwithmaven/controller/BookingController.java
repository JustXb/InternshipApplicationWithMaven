package com.example.intershipapplicationwithmaven.controller;

import com.example.EventType;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("bookings")
public class BookingController {
    BookingService bookingService;
    private final RestTemplate restTemplate = new RestTemplate();

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
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
    public ResponseEntity<String> addGuest(@RequestBody GuestEntity guestEntity) {
        try {
            bookingService.addGuest(guestEntity);
            bookingService.sendEvent(EventType.CREATED, "Гость добавлен: " + guestEntity.toString());

            return ResponseEntity.ok("Гость успешно добавлен: " + guestEntity.getName());
        } catch (ResponseStatusException ex) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка добавления гостя: " + ex.getReason());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getReason());
        } catch (IOException e) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при добавлении гостя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении гостя.");
        }
    }



    @PutMapping("/updateGuest/{id}")
    public ResponseEntity<String> updateGuest(@PathVariable int id, @RequestBody GuestEntity guestEntity) {
        try {
            bookingService.updateGuestHttp(id, guestEntity);
            bookingService.sendEvent(EventType.SUCCESS, "Гость с ID " + id + " успешно обновлен.");
            return ResponseEntity.ok("Гость с ID " + id + " успешно обновлен.");
        } catch (ResponseStatusException ex) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при обновлении гостя с ID " + id + ": " + ex.getReason());
            return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
        } catch (Exception e) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при обновлении гостя с ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении гостя.");
        }
    }

    @DeleteMapping("/deleteGuest/{id}")
    public ResponseEntity<String> deleteGuest(@PathVariable int id) {
        try {
            Integer hotelId = bookingService.getHotelId(id);

            if (hotelId != 0) {

                String availabilityUrl = "http://localhost:8081/hotels/increaseAvailability?hotelId=" + hotelId;
                ResponseEntity<String> availabilityResponse = restTemplate.postForEntity(availabilityUrl, null, String.class);
                bookingService.deleteGuestHttp(id);
                bookingService.sendEvent(EventType.SUCCESS, "Гость с ID " + id + " успешно удален. Доступность отеля " + hotelId + " увеличена.");

                return ResponseEntity.ok("Гость с ID " + id + " успешно удален, доступность отеля " + hotelId + " увеличена.");
            } else {
                bookingService.deleteGuestHttp(id);
                bookingService.sendEvent(EventType.SUCCESS, "Гость с ID " + id + " успешно удален. Гость не проживал в отеле.");

                return ResponseEntity.ok("Гость с ID " + id + " успешно удален, гость не проживал в отеле.");
            }
        } catch (ResponseStatusException ex) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при удалении гостя с ID " + id + ": " + ex.getReason());
            return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
        } catch (Exception e) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при удалении гостя с ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении гостя. " + e.getMessage());
        }
    }


    @PostMapping("/checkIn")
    public ResponseEntity<String> checkIn(@RequestBody Map<String, Integer> request) {
        Integer guestId = request.get("guestId");
        Integer hotelId = request.get("hotelId");

        try {
            GuestEntity guestEntity = bookingService.getGuestByID(guestId);

            bookingService.validateCheckInHttp(guestId, hotelId);

            String availabilityUrl = "http://localhost:8081/hotels/checkAvailability?hotelId=" + hotelId;
            ResponseEntity<String> availabilityResponse = restTemplate.postForEntity(availabilityUrl, null, String.class);

            if (availabilityResponse.getBody().equals("AVAILABLE")) {
                bookingService.bookRoom(guestEntity, hotelId);
                bookingService.sendEvent(EventType.SUCCESS, "Гость с ID " + guestId + " успешно заселен в отель " + hotelId);
                return ResponseEntity.ok("Гость " + guestId + " успешно заселен в отель " + hotelId);
            } else if (availabilityResponse.getBody().equals("UNAVAILABLE")) {
                bookingService.sendEvent(EventType.MISTAKE, "Ошибка заселения: отель " + hotelId + " недоступен.");
                return ResponseEntity.badRequest().body("Отель недоступен.");
            } else {
                bookingService.sendEvent(EventType.MISTAKE, "Ошибка заселения: в отеле " + hotelId + " нет свободных мест.");
                return ResponseEntity.badRequest().body("В отеле нет свободных мест.");
            }
        } catch (Exception e) {
            bookingService.sendEvent(EventType.MISTAKE, "Ошибка при заселении гостя с ID " + guestId + ": " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при заселении гостя.");
        }
    }



}
