package com.example.intershipapplicationwithmaven.controller;

import com.example.EventType;
import com.example.intershipapplicationwithmaven.exception.EnteredNotValidDataException;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("bookings")
public class BookingController {
    BookingService bookingService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String HOTEL_SERVICE_URL = "http://localhost:8081/hotels";
    private static final String CHECK_AVAILABILITY_URL = "/checkAvailability?hotelId=";
    private static final String GET_ALL_HOTELS = "/getAllHotels";
    private static final String INCREASE_AVAILABILITY = "/increaseAvailability?hotelId=";

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
            bookingService.sendBookingToMonitoring(EventType.CREATED, ControllerMessages.GUEST_ADDED.getMessage(guestEntity.toString()));
            return ResponseEntity.ok(ControllerMessages.GUEST_ADDED.getMessage(guestEntity.getName()));
        }
        catch (EnteredNotValidDataException e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (ResponseStatusException ex) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE,  ControllerMessages.ADD_GUEST_ERROR.getMessage(ex.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getReason());
        } catch (Exception e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE,  ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
        }
    }



    @PutMapping("/updateGuest/{id}")
    public ResponseEntity<String> updateGuest(@PathVariable int id, @RequestBody GuestEntity guestEntity) {
        try {
            bookingService.updateGuestHttp(id, guestEntity);
            bookingService.sendBookingToMonitoring(EventType.SUCCESS, ControllerMessages.GUEST_UPDATED.getMessage(id));
            return ResponseEntity.ok(ControllerMessages.GUEST_UPDATED.getMessage(id));
        }
        catch (EnteredNotValidDataException e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (ResponseStatusException ex) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE,  ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, ex.getReason()));
            return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
        } catch (Exception e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }

    @DeleteMapping("/deleteGuest/{id}")
    public ResponseEntity<String> deleteGuest(@PathVariable int id) {
        try {
            int hotelId = bookingService.getHotelId(id);
            if (hotelId != 0) {
                try {
                    restTemplate.getForEntity(HOTEL_SERVICE_URL + GET_ALL_HOTELS, String.class);
                } catch (Exception e) {
                    bookingService.sendBookingToMonitoring(EventType.MISTAKE,
                            ControllerMessages.DELETE_GUEST_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage());
                }

                bookingService.deleteGuestHttp(id);
                restTemplate.postForEntity(HOTEL_SERVICE_URL + INCREASE_AVAILABILITY + hotelId, null, String.class);
                bookingService.sendBookingToMonitoring(EventType.SUCCESS, ControllerMessages.GUEST_DELETED.getMessage(id, hotelId));

                return ResponseEntity.ok(ControllerMessages.GUEST_DELETED.getMessage(id, hotelId));
            } else {
                bookingService.deleteGuestHttp(id);
                bookingService.sendBookingToMonitoring(EventType.SUCCESS, ControllerMessages.GUEST_DELETED_WITHOUT_HOTEL.getMessage(id));

                return ResponseEntity.ok(ControllerMessages.GUEST_DELETED_WITHOUT_HOTEL.getMessage(id));
            }
        } catch (ResponseStatusException ex) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, ex.getReason()));
            return ResponseEntity.status(ex.getStatus()).body(ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, ex.getReason()));
        } catch (Exception e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }


    @PostMapping("/checkIn")
    public ResponseEntity<String> checkIn(@RequestBody Map<String, Integer> request) {
        Integer guestId = request.get("guestId");
        Integer hotelId = request.get("hotelId");
        if (guestId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.GUEST_ID_NOT_NULL.getMessage()));
        }
        if (hotelId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_ID_NOT_NULL.getMessage()));
        }

        try {
            GuestEntity guestEntity = bookingService.getGuestByID(guestId);
            ResponseEntity<String> validationResponse = bookingService.validateCheckInHttp(guestId, hotelId);
            if (!validationResponse.getStatusCode().is2xxSuccessful()) {
                bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.CHECK_IN_ERROR.getMessage(validationResponse.getBody()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ControllerMessages.CHECK_IN_ERROR.getMessage(validationResponse.getBody()));
            }

            try {
                restTemplate.getForEntity(HOTEL_SERVICE_URL + GET_ALL_HOTELS, String.class);
            } catch (Exception e) {
                bookingService.sendBookingToMonitoring(EventType.MISTAKE,
                        ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage());
            }

            ResponseEntity<String> availabilityResponse = restTemplate.postForEntity(HOTEL_SERVICE_URL +
                    CHECK_AVAILABILITY_URL + hotelId, null, String.class);

            if (ControllerMessages.AVAILABLE.getMessage().equals(availabilityResponse.getBody())) {
                bookingService.bookRoom(guestEntity, hotelId);
                bookingService.sendBookingToMonitoring(EventType.SUCCESS, ControllerMessages.CHECK_IN_SUCCESS.getMessage(guestId, hotelId));
                return ResponseEntity.ok(ControllerMessages.CHECK_IN_SUCCESS.getMessage(guestId, hotelId));
            } else if (ControllerMessages.UNAVAILABLE.getMessage().equals(availabilityResponse.getBody())) {
                bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.CHECK_IN_AVAILABILITY_ERROR.getMessage(hotelId));
                return ResponseEntity.badRequest().body(ControllerMessages.CHECK_IN_AVAILABILITY_ERROR.getMessage(hotelId));
            } else {
                bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.CHECK_IN_NO_VACANCY.getMessage(hotelId));
                return ResponseEntity.badRequest().body(ControllerMessages.CHECK_IN_NO_VACANCY.getMessage(hotelId));
            }

        } catch (ResponseStatusException ex) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(guestId, ex.getReason()));
            return ResponseEntity.status(ex.getStatus()).body(ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(guestId, ex.getReason()));

        } catch (Exception e) {
            bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(guestId, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(guestId, e.getMessage()));
        }
    }


}