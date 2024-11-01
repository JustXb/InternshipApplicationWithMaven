package com.internshipbooking.controller;
//TODO: fix messages(error)
import com.example.request.HotelDTO;
import com.internshipbooking.exception.EnteredNotValidDataException;
import com.internshipbooking.exception.GuestNotFoundException;
import com.internshipbooking.repository.entity.GuestEntity;
import com.internshipbooking.service.BookingService;
import com.internshipbooking.transport.dto.request.BookingDto;
import com.internshipbooking.transport.dto.request.GuestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("bookings")
public class BookingController {
    BookingService bookingService;


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/getAllGuests")
    public ResponseEntity<List<GuestDTO>> getAllGuests(){
        List<GuestDTO> guests = bookingService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/getAllHotels")
    public ResponseEntity<List<HotelDTO>> getAllHotels(){
        return ResponseEntity.ok(bookingService.getAllHotels());
    }

    @GetMapping("/getGuest/{id}")
    public ResponseEntity<GuestEntity> getGuest(@PathVariable int id) throws GuestNotFoundException {
        GuestEntity guest = bookingService.getGuestByID(id);
        return ResponseEntity.ok(guest);
    }

    @PostMapping("/addGuest")
    public ResponseEntity<String> addGuest(@RequestBody @Valid GuestDTO guestDto) {
        try {
            int guestId = bookingService.addGuest(guestDto);
            return ResponseEntity.ok(ControllerMessages.GUEST_ADDED.getMessage(guestId));
        }
        catch (EnteredNotValidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
        }
    }



    @PutMapping("/updateGuest/{id}")
    public ResponseEntity<String> updateGuest(@PathVariable int id,
                                              @RequestBody @Valid GuestDTO guestDTO) {
        try {
            bookingService.updateGuestHttp(id, guestDTO);
            return ResponseEntity.ok(ControllerMessages.GUEST_UPDATED.getMessage(id));
        }
        catch (EnteredNotValidDataException | GuestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }

    @DeleteMapping("/deleteGuest/{id}")
    public ResponseEntity<String> deleteGuest(@PathVariable int id) {
        try {
            String resultMessage = bookingService.deleteGuestHttp(id);
            return ResponseEntity.ok(resultMessage);
        } catch (GuestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ControllerMessages.GUEST_NOT_FOUND.getMessage(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }


    @PostMapping("/checkIn")
    public ResponseEntity<String> checkIn(@RequestBody BookingDto bookingDto) {
        try {
            String resultMessage = bookingService.checkInHttp(bookingDto);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ControllerMessages.CHECK_IN_ERROR.getMessage(e.getMessage()));
        } catch (GuestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(bookingDto.getGuestId(), e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ControllerMessages.CHECK_IN_ERROR_WITH_ID.getMessage(bookingDto.getGuestId(), e.getMessage()));
        }
    }





}