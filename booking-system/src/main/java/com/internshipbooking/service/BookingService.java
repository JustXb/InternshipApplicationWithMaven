package com.internshipbooking.service;


import com.example.EventType;
import com.internshipbooking.controller.ControllerMessages;
import com.internshipbooking.exception.EnteredNotValidDataException;
import com.internshipbooking.exception.GuestNotFoundException;
import com.internshipbooking.producer.BookingProducer;
import com.internshipbooking.repository.entity.BookingEntity;
import com.internshipbooking.repository.entity.GuestEntity;
import com.internshipbooking.repository.impl.BookingRepository;
import com.internshipbooking.repository.impl.GuestRepository;
import com.internshipbooking.util.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingProducer bookingProducer;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final Mapper mapper;


    @Autowired
    public BookingService(BookingRepository bookingRepository, GuestRepository guestRepository,
                          Mapper mapper, BookingProducer bookingProducer) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.bookingProducer = bookingProducer;
    }


    public void bookRoom(GuestEntity guestId, int hotelId) {
        BookingEntity booking = new BookingEntity(guestId, hotelId);
        bookingRepository.save(booking);
        System.out.println(ServiceMessages.CHECK_IN_SUCCESS.getMessage(guestId.getId(), hotelId));
    }

    public boolean validateCheckInGuest(int guestId) {
        return guestRepository.existsById(guestId);
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

    public String validateCreateGuestHttp(GuestEntity guestEntity) throws EnteredNotValidDataException {
        if (validateName(guestEntity.getName())) {
            return ServiceMessages.WRONG_NAME.getMessage();
        }
        if (validateAge(guestEntity.getAge())) {
            return ServiceMessages.WRONG_AGE.getMessage();
        }
        if (validateAddress(guestEntity.getAddress())) {
            return ServiceMessages.WRONG_ADDRESS.getMessage();
        }
        if (validatePassportNumber(guestEntity.getPassportNumber())) {
            return ServiceMessages.WRONG_PASSPORT.getMessage();
        }
        return null;
    }

    public String validateUpdateGuestHttp(GuestEntity guestEntity) throws EnteredNotValidDataException {
        if (validateName(guestEntity.getName())) {
            return ServiceMessages.WRONG_NAME.getMessage();
        }
        if (validateAge(guestEntity.getAge())) {
            return ServiceMessages.WRONG_AGE.getMessage();
        }
        if (validateAddress(guestEntity.getAddress())) {
            return ServiceMessages.WRONG_ADDRESS.getMessage();
        }

        if (guestEntity.getPassportNumber() == null || guestEntity.getPassportNumber().length() != 6 ||
                !guestEntity.getPassportNumber().matches("\\d{6}")) {
            bookingProducer.sendBookingToMonitoring
                    (EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage
                            (guestEntity.getId(), ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
        }

        return null;
    }

    private boolean validateName(String name) throws EnteredNotValidDataException {
        if(name == null || name.trim().isEmpty()){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_EMPTY_NAME.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_EMPTY_NAME.getMessage());
        }
        if(name.length() > 20){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_NAME.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_NAME.getMessage());
        }

        if(!Character.isUpperCase(name.charAt(0))){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_NAME.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_NAME.getMessage());
        }

        return name == null || name.length() > 20 || !Character.isUpperCase(name.charAt(0)) || name.trim().isEmpty();
    }

    private boolean validateAge(int age) {
        try {
            return age < 0 || age > 120;
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("Возраст должен быть числом");
        }
    }

    private boolean validatePassportNumber(String passportNumber) throws EnteredNotValidDataException {
        if (passportNumber != null && passportNumber.length() == 6 && passportNumber.matches("\\d{6}")) {
            boolean passportExists = true;
            List<GuestEntity> guests = guestRepository.findAll();
            for (GuestEntity guest : guests) {
                if (guest.getPassportNumber().equals(passportNumber)) {
                    bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                            getMessage(ServiceMessages.EXISTING_GUEST.getMessage()));
                    throw new EnteredNotValidDataException(ServiceMessages.EXISTING_GUEST.getMessage());
                }
            }
            return !passportExists;
        } else {
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
        }
    }


    private boolean validateAddress(String address) throws EnteredNotValidDataException {
        if(address == null || address.trim().isEmpty()){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_EMPTY_ADDRESS.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_EMPTY_ADDRESS.getMessage());
        }
        if(address.length() > 20){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_ADDRESS.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_ADDRESS.getMessage());
        }

        if(!Character.isUpperCase(address.charAt(0))){
            bookingProducer.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.ADD_GUEST_ERROR.
                    getMessage(ServiceMessages.ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_ADDRESS.getMessage()));
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_ADDRESS.getMessage());
        }

        return address == null || address.length() > 30 || !Character.isUpperCase(address.charAt(0));
    }

    public List<GuestEntity> getAllGuests() {
        return guestRepository.findAllByOrderByIdAsc();
    }

    public GuestEntity getGuestByID(int id) throws GuestNotFoundException {
        if (guestRepository.existsById(id)) {
            return guestRepository.findById(id).get();
        } else {
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }
    }


    public void addGuest(GuestEntity guestEntity) throws EnteredNotValidDataException {
        String validationError = validateCreateGuestHttp(guestEntity);
        if (validationError != null) {
            throw new EnteredNotValidDataException(validationError);
        }
        guestRepository.save(guestEntity);
        bookingProducer.sendBookingToMonitoring(EventType.CREATED,
                ControllerMessages.GUEST_ADDED.getMessage(guestEntity.toString()));
    }

    public void deleteGuestHttp(int id) throws GuestNotFoundException {
        if (guestRepository.existsById(id)) {
            guestRepository.deleteById(id);
        } else {
            throw new GuestNotFoundException(ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
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


    public void updateGuestHttp(int id, GuestEntity guest) throws EnteredNotValidDataException, GuestNotFoundException {
        if (guestRepository.existsById(id)) {
            String validationError = validateUpdateGuestHttp(guest);
            if (validationError != null) {
                bookingProducer.sendBookingToMonitoring
                        (EventType.MISTAKE, ControllerMessages.UPDATE_GUEST_ERROR.getMessage(id, validationError));
                throw new EnteredNotValidDataException(validationError);
            }
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

    public String validateCheckInHttp(int guestId) {
        if (!validateCheckInGuest(guestId)) {
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
}
