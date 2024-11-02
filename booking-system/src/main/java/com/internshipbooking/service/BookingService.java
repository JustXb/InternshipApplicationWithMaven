package com.internshipbooking.service;


import com.example.EventType;
import com.example.request.HotelDTO;
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
import com.internshipbooking.transport.dto.request.GuestDTO;
import com.internshipbooking.mapper.Mapper;

import com.internshipbooking.webclient.HotelsWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public String checkInHttp(BookingDto bookingDto) throws GuestNotFoundException, Exception {

        int guestId = bookingDto.getGuestId();
        int hotelId = bookingDto.getHotelId();

        if (guestId == 0) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.GUEST_ID_NOT_NULL.getMessage());
            throw new IllegalArgumentException(ControllerMessages.GUEST_ID_NOT_NULL.getMessage());
        }
        if (hotelId == 0) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.HOTEL_ID_NOT_NULL.getMessage());
            throw new IllegalArgumentException(ControllerMessages.HOTEL_ID_NOT_NULL.getMessage());
        }

        try {
            getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
            throw new Exception(ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
        }

        GuestEntity guestEntity = getGuestByID(guestId);
        String validationResult = validateCheckInHttp(guestId);

        if (ControllerMessages.WRONG_GUEST_ID.getMessage().equals(validationResult) ||
                ControllerMessages.EXIST_CHECKIIN.getMessage().equals(validationResult)) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_ERROR.getMessage(validationResult));
            throw new IllegalArgumentException(validationResult);
        }

        ResponseEntity<String> availabilityResponse = checkHotelAvailability(hotelId);
        if ("UNAVAILABLE_NOAVAILABILITY".equals(availabilityResponse.getBody())) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_NO_VACANCY.getMessage(hotelId));
            throw new IllegalStateException(ControllerMessages.CHECK_IN_NO_VACANCY.getMessage(hotelId));
        }

        if (ControllerMessages.UNAVAILABLE.getMessage().equals(availabilityResponse.getBody())) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_AVAILABILITY_ERROR.getMessage(hotelId));
            throw new IllegalStateException(ControllerMessages.CHECK_IN_AVAILABILITY_ERROR.getMessage(hotelId));
        }


        bookRoom(guestEntity, hotelId);
        bookingProducer.sendBookingToMonitoring(EventType.SUCCESS,
                ControllerMessages.CHECK_IN_SUCCESS.getMessage(guestId, hotelId));
        return ControllerMessages.CHECK_IN_SUCCESS.getMessage(guestId, hotelId);
    }

    public void bookRoom(GuestEntity guestEntity, int hotelId) {
        BookingEntity booking = new BookingEntity(guestEntity, hotelId);
        bookingRepository.save(booking);
    }


    public boolean validateBookingDoubleCheckIn(int guestId) {
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
            for (GuestEntity guest : guests) {
                if (guest.getPassportNumber().equals(passportNumber)) {
                    bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                            getMessage(ServiceMessages.EXISTING_GUEST.getMessage()));
                    throw new EnteredNotValidDataException(ServiceMessages.EXISTING_GUEST.getMessage());
                }
            }
    }



    public List<GuestDTO> getAllGuests() {
        List<GuestDTO> guestDTOs = new ArrayList<>();
        List<GuestEntity> guests = guestRepository.findAllByOrderByIdAsc();
        for(GuestEntity guest: guests){
                guestDTOs.add(mapper.toDto(guest));
        }
        return guestDTOs;
    }

    public GuestEntity getGuestByID(int id) throws GuestNotFoundException {
        if (guestRepository.existsById(id)) {
            return guestRepository.findById(id).get();
        } else {
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }
    }


    public int addGuest(GuestDTO guestDTO) throws EnteredNotValidDataException {
        try {
            GuestEntity guestEntity = mapper.toEntity(guestDTO);

            validatePassportNumber(guestEntity.getPassportNumber());


            guestRepository.save(guestEntity);
            bookingProducer.sendBookingToMonitoring(EventType.CREATED,
                    ControllerMessages.GUEST_ADDED.getMessage(guestEntity.toString()));
            return guestEntity.getId();
        }
        catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
            throw new RuntimeException(ControllerMessages.ADD_GUEST_ERROR.getMessage(e.getMessage()));
        }
    }

    public String deleteGuestHttp(int id) throws GuestNotFoundException, HotelUnavailableException {
        if (!guestRepository.existsById(id)) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }

        try {
            getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
            throw new HotelUnavailableException(ControllerMessages.DELETE_GUEST_ERROR.getMessage(id, ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
        }

        int hotelId = getHotelId(id);

        guestRepository.deleteById(id);

        if (hotelId != 0) {
            increaseHotelAvailability(hotelId);
            bookingProducer.sendBookingToMonitoring(EventType.SUCCESS,
                    ControllerMessages.GUEST_DELETED.getMessage(id, hotelId));
            return ControllerMessages.GUEST_DELETED.getMessage(id, hotelId);
        } else {
            bookingProducer.sendBookingToMonitoring(EventType.SUCCESS,
                    ControllerMessages.GUEST_DELETED_WITHOUT_HOTEL.getMessage(id));
            return ControllerMessages.GUEST_DELETED_WITHOUT_HOTEL.getMessage(id);
        }
    }

    public int getHotelId(int guestId) throws GuestNotFoundException {
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


    public void updateGuestHttp(int id, GuestDTO guestDto) throws EnteredNotValidDataException, GuestNotFoundException {
        try {
            GuestEntity guest = mapper.toEntity(guestDto);

            if (guestRepository.existsById(id)) {

                boolean passportExists = guestRepository.existsByPassportNumberAndIdNot(guest.getPassportNumber(), id);
                if (passportExists) {
                    bookingProducer.sendBookingToMonitoring
                            (EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, guest.getPassportNumber()));
                    throw new EnteredNotValidDataException(ServiceMessages.GUEST_WITH_PASSPORT_EXIST.getMessage(guest.getPassportNumber()));
                } else {
                    guest.setId(id);
                    guestRepository.save(guest);
                    bookingProducer.sendBookingToMonitoring
                            (EventType.SUCCESS, ControllerMessages.GUEST_UPDATED.getMessage(id));

                }
            } else {
                bookingProducer.sendBookingToMonitoring
                        (EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, ServiceMessages.GUEST_NOT_FOUND.getMessage(id)));
                throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
            }
        }
        catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
            throw new RuntimeException(ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, e.getMessage()));
        }
    }

    public String validateCheckInHttp(int guestId) {
        if (!guestRepository.existsById(guestId)) {
            return ServiceMessages.WRONG_GUEST_ID.getMessage();
        }

        if (!validateBookingDoubleCheckIn(guestId)) {
            return ServiceMessages.EXIST_CHECKIIN.getMessage();
        }

        return ServiceMessages.ACCESS_CHECKIN.getMessage();
    }


    public void sendBookingToMonitoring(EventType eventType, String message) {
        bookingProducer.sendBookingToMonitoring(eventType, message);
    }


    public List<HotelDTO> getAllHotels() throws Exception {
        try {
            hotelsWebClient.getAllHotels();
        } catch (Exception e) {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE,
                    ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
            throw new Exception(ControllerMessages.CHECK_IN_ERROR.getMessage(ControllerMessages.HOTEL_SERVICE_UNAVAILABLE.getMessage()));
        }
        return hotelsWebClient.getAllHotels();
    }

    public ResponseEntity<String> checkHotelAvailability(int hotelId) {
        return hotelsWebClient.checkAvailability(hotelId);
    }

    public ResponseEntity<String> increaseHotelAvailability(int hotelId) {
        return hotelsWebClient.increaseAvailability(hotelId);
    }
}
