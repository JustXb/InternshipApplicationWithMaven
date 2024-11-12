package com.internshipbooking.service;



import com.example.EventType;
import com.example.request.HotelDTO;

import static com.example.EventType.*;
import static com.internshipbooking.controller.ControllerMessages.*;
import static com.internshipbooking.service.ServiceMessages.*;

import com.internshipbooking.controller.ControllerMessages;
import com.internshipbooking.exception.EnteredNotValidDataException;
import com.internshipbooking.exception.GuestNotFoundException;
import com.internshipbooking.exception.HotelUnavailableException;
import com.internshipbooking.producer.BookingProducer;
import com.internshipbooking.repository.entity.BookingEntity;
import com.internshipbooking.repository.entity.GuestEntity;
import com.internshipbooking.repository.impl.BookingRepository;
import com.internshipbooking.repository.impl.GuestRepository;
import com.internshipbooking.transport.dto.request.BookingDto;
import com.internshipbooking.transport.dto.request.GuestDto;
import com.internshipbooking.mapper.Mapper;

import com.internshipbooking.webclient.HotelsWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.internshipbooking.service.ServiceMessages.EXIST_CHECK_IN;
import static com.internshipbooking.service.ServiceMessages.WRONG_GUEST_ID;


@Service
public class BookingService {
    private final HotelsWebClient hotelsWebClient;
    private final BookingProducer bookingProducer;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final Mapper mapper;


    @Autowired
    public BookingService(BookingRepository bookingRepository, GuestRepository guestRepository,
                          Mapper mapper, BookingProducer bookingProducer, HotelsWebClient hotelsWebClient) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.bookingProducer = bookingProducer;
        this.hotelsWebClient = hotelsWebClient;
    }


    public String checkInHttp(BookingDto bookingDto) throws Exception {

        int guestId = bookingDto.getGuestId();
        int hotelId = bookingDto.getHotelId();

        if (guestId == 0) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    GUEST_ID_NOT_NULL.getMessage());
            throw new IllegalArgumentException(GUEST_ID_NOT_NULL.getMessage());
        }
        if (hotelId == 0) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    HOTEL_ID_NOT_NULL.getMessage());
            throw new IllegalArgumentException(HOTEL_ID_NOT_NULL.getMessage());
        }

        try {
            getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    CHECK_IN_ERROR.getMessage(HOTEL_SERVICE_UNAVAILABLE.getMessage()));
            throw new HotelUnavailableException(CHECK_IN_ERROR.getMessage(HOTEL_SERVICE_UNAVAILABLE.getMessage()));
        }

        GuestEntity guestEntity = getGuestByID(guestId);
        ServiceMessages validationResult = validateCheckInHttp(guestId);
        String stringResult = validationResult.getMessage();

        if (WRONG_GUEST_ID.equals(validationResult) ||
                EXIST_CHECK_IN.equals(validationResult)) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    CHECK_IN_ERROR.getMessage(stringResult));
            throw new IllegalArgumentException(stringResult);
        }

        ResponseEntity<String> availabilityResponse = checkHotelAvailability(hotelId);
        String noVacancy = CHECK_IN_NO_VACANCY.getMessage(hotelId);
        if (UNAVAILABLE_NOAVAILABILITY.name().equals(availabilityResponse.getBody())) {

            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    CHECK_IN_NO_VACANCY.getMessage(hotelId));
            throw new IllegalStateException(noVacancy);
        }

        if (UNAVAILABLE.name().equals(availabilityResponse.getBody())) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    noVacancy);
            throw new IllegalStateException(CHECK_IN_AVAILABILITY_ERROR.getMessage(hotelId));
        }


        bookRoom(guestEntity, hotelId);
        String checkInSuccess = CHECK_IN_SUCCESS.getMessage(guestId, hotelId);
        bookingProducer.sendBookingToMonitoring(SUCCESS,
                checkInSuccess);
        return checkInSuccess;
    }

    private void bookRoom(GuestEntity guestEntity, int hotelId) {
        BookingEntity booking = new BookingEntity(guestEntity, hotelId);
        bookingRepository.save(booking);
    }


    private boolean validateBookingDoubleCheckIn(int guestId) {
        List<BookingEntity> bookings = bookingRepository.findAll();
        for (BookingEntity booking : bookings) {
            if (booking.getGuest().getId() == guestId) {
                return false;
            }
        }
        return true;
    }


    private void validatePassportNumber(String passportNumber) throws EnteredNotValidDataException {
            List<GuestEntity> guests = guestRepository.findAll();
            String existingGuest = EXISTING_GUEST.getMessage();
            for (GuestEntity guest : guests) {
                if (guest.getPassportNumber().equals(passportNumber)) {
                    bookingProducer.sendBookingToMonitoring(MISTAKE, ADD_GUEST_ERROR.
                            getMessage(existingGuest));
                    throw new EnteredNotValidDataException(existingGuest);
                }
            }
    }



    public List<GuestDto> getAllGuests() {
        List<GuestDto> guestDtos = new ArrayList<>();
        List<GuestEntity> guests = guestRepository.findAllByOrderByIdAsc();
        for(GuestEntity guest: guests){
                guestDtos.add(mapper.toDto(guest));
        }
        return guestDtos;
    }

    public GuestEntity getGuestByID(int id) throws GuestNotFoundException {
        return guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id)));
    }



    public int addGuest(GuestDto guestDTO) throws EnteredNotValidDataException {
        try {
            GuestEntity guestEntity = mapper.toEntity(guestDTO);
            validatePassportNumber(guestEntity.getPassportNumber());


            guestRepository.save(guestEntity);
            bookingProducer.sendBookingToMonitoring(CREATED,
                    GUEST_ADDED.getMessage(guestEntity.toString()));
            return guestEntity.getId();
        }
        catch (Exception e) {
            String addGuestError = ADD_GUEST_ERROR.getMessage(e.getMessage());
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    addGuestError);
            throw new RuntimeException(addGuestError);
        }
    }

    public String deleteGuestHttp(int id) throws GuestNotFoundException, HotelUnavailableException {
        if (!guestRepository.existsById(id)) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }

        try {
            getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    CHECK_IN_ERROR.getMessage(HOTEL_SERVICE_UNAVAILABLE.getMessage()));

            String errorMessage = DELETE_GUEST_ERROR.getMessage(id, HOTEL_SERVICE_UNAVAILABLE.getMessage());
            throw new HotelUnavailableException(errorMessage);

        }

        int hotelId = getHotelId(id);

        guestRepository.deleteById(id);

        if (hotelId != 0) {
            hotelsWebClient.increaseAvailability(hotelId);
            String guestDeleted = GUEST_DELETED.getMessage(id, hotelId);
            bookingProducer.sendBookingToMonitoring(SUCCESS,
                    guestDeleted);
            return guestDeleted;
        } else {
            String guestDeletedWithoutHotel = GUEST_DELETED_WITHOUT_HOTEL.getMessage(id);
            bookingProducer.sendBookingToMonitoring(SUCCESS,
                    guestDeletedWithoutHotel);
            return guestDeletedWithoutHotel;
        }
    }


    private int getHotelId(int guestId) throws GuestNotFoundException {
        Optional<GuestEntity> optionalGuest = guestRepository.findById(guestId);

        if (optionalGuest.isPresent()) {
            GuestEntity guest = optionalGuest.get();
            if (guest.getBooking() != null) {
                BookingEntity booking = guest.getBooking();
                return booking.getHotelId();
            }
            else {
                return 0;
            }
        } else {
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(guestId));
        }
    }


    public void updateGuestHttp(int id, GuestDto guestDto) throws EnteredNotValidDataException, GuestNotFoundException {
        try {
            GuestEntity guest = mapper.toEntity(guestDto);
            guest.setBooking(guestRepository.findById(id).get().getBooking());

            if (guestRepository.existsById(id)) {
                boolean passportExists = guestRepository.existsByPassportNumberAndIdNot(guest.getPassportNumber(), id);
                if (passportExists) {
                    bookingProducer.sendBookingToMonitoring
                            (MISTAKE, UPDATE_GUEST_ERROR.getMessage(id, guest.getPassportNumber()));
                    throw new EnteredNotValidDataException(GUEST_WITH_PASSPORT_EXIST.getMessage(guest.getPassportNumber()));
                } else {
                    guest.setId(id);
                    guestRepository.save(guest);
                    bookingProducer.sendBookingToMonitoring
                            (SUCCESS, GUEST_UPDATED.getMessage(id));

                }
            } else {
                bookingProducer.sendBookingToMonitoring
                        (MISTAKE, UPDATE_GUEST_ERROR.getMessage(id, ServiceMessages.GUEST_NOT_FOUND.getMessage(id)));
                throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
            }
        }
        catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
            throw new RuntimeException(UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }

    public ServiceMessages validateCheckInHttp(int guestId) {
        if (!guestRepository.existsById(guestId)) {
            return WRONG_GUEST_ID;
        }

        if (!validateBookingDoubleCheckIn(guestId)) {
            return EXIST_CHECK_IN;
        }

        return ACCESS_CHECKIN;
    }


    public void sendBookingToMonitoring(EventType eventType, String message) {
        bookingProducer.sendBookingToMonitoring(eventType, message);
    }


    public List<HotelDTO> getAllHotels() throws Exception {
        try {
            hotelsWebClient.getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(MISTAKE,
                    CHECK_IN_ERROR.getMessage(HOTEL_SERVICE_UNAVAILABLE.getMessage()));
            throw new Exception(CHECK_IN_ERROR.getMessage(HOTEL_SERVICE_UNAVAILABLE.getMessage()));
        }
        return hotelsWebClient.getAllHotels();
    }


    private ResponseEntity<String> checkHotelAvailability(int hotelId) {
        return hotelsWebClient.checkAvailability(hotelId);
    }

}
