package com.example.intershipapplicationwithmaven.service;


import com.example.EventType;
import com.example.MonitoringEvent;
import com.example.intershipapplicationwithmaven.config.RabbitMQConfig;
import com.example.intershipapplicationwithmaven.exception.EnteredNotValidDataException;
import com.example.intershipapplicationwithmaven.repository.entity.BookingEntity;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.repository.impl.BookingRepository;
import com.example.intershipapplicationwithmaven.repository.impl.GuestRepository;
import com.example.intershipapplicationwithmaven.transport.dto.request.GuestDTO;
import com.example.intershipapplicationwithmaven.util.Mapper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

@Service
public class BookingService {
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final Mapper mapper = new Mapper();
    private final Scanner scanner;
    RabbitTemplate rabbitTemplate;
    RestTemplate restTemplate;

    private final Logger LOGGER = Logger.getLogger(BookingService.class.getName());

    @Autowired
    public BookingService(BookingRepository bookingRepository, GuestRepository guestRepository,
                          RabbitTemplate rabbitTemplate) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.scanner = new Scanner(System.in);
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = new RestTemplate();

    }

    public void createGuest() throws EnteredNotValidDataException {
        String firstName = null;
        int age = 0;
        String address = null;
        String passport = null;

        System.out.println("Если вы хотите закончить ввод - введите exit");

        firstName = getFirstName(null);
        if (firstName == null) return;  // Прерывание метода

        // Ввод возраста с повторной попыткой в случае ошибки
        while (age == 0) {
            try {
                String ageInput = getValidInput(ServiceMessages.ENTER_AGE.getMessage(),
                        ServiceMessages.ERROR_MESSAGE_EMPTY_AGE.getMessage());
                if (ageInput.equalsIgnoreCase("exit")) {
                    System.out.println("Введите help для получения помощи");
                    return;  // Прерывание метода
                }
                age = Integer.parseInt(ageInput);


                // Валидация возраста
                if (validateAge(age)) {
                    System.out.println(ServiceMessages.WRONG_AGE.getMessage());
                    age = 0; // повторить ввод
                }
            } catch (NumberFormatException e) {
                System.out.println(ServiceMessages.ERROR_AGE_NOT_INT.getMessage());
                age = 0; // повторить ввод
            }
        }

        // Ввод адреса с повторной попыткой в случае ошибки
        address = getAddress(address);
        if (address == null) return;  // Прерывание метода

        // Ввод паспорта с повторной попыткой в случае ошибки
        passport = getPassport(passport);
        if (passport == null) return;  // Прерывание метода

        // Создание DTO и сущности после успешного ввода всех данных
        try {
            GuestDTO guestDTO = new GuestDTO(firstName, age, passport, address);
            GuestEntity guest = mapper.toEntity(guestDTO);

            if (validateCreateGuest(guest)) {
                guestRepository.save(guest);
            }
        } catch (IOException e) {
            System.out.println(ServiceMessages.ERROR_CREATE_GUEST.getMessage());
        } catch (Exception e) {
            LOGGER.severe(ServiceMessages.UNKNOWN_ERROR.getMessage(e.getMessage()));
        }
    }

    private String getFirstName(String firstName) throws EnteredNotValidDataException {
        // Ввод имени с повторной попыткой в случае ошибки
        while (firstName == null) {
            firstName = getValidInput(ServiceMessages.ENTER_NAME.getMessage(),
                    ServiceMessages.ERROR_MESSAGE_EMPTY_NAME.getMessage());

            if (firstName.equalsIgnoreCase("exit")) {
                System.out.println("Введите help для получения помощи");
                return null;
            }

            // Валидация имени
            if (validateName(firstName)) {
                System.out.println(ServiceMessages.WRONG_NAME.getMessage());
                firstName = null; // повторить ввод
            }
        }
        return firstName;
    }

    private String getPassport(String passport) {
        while (passport == null) {
            try {
                passport = getValidInput(ServiceMessages.ENTER_PASSPORT.getMessage(),
                        ServiceMessages.ERROR_MESSAGE_EMPTY_PASSPORT.getMessage());

                if (passport.equalsIgnoreCase("exit")) {
                    System.out.println("Введите help для получения помощи");
                    return null;
                }

                // Валидация паспорта
                if (validatePassportNumber(passport)) {
                    System.out.println(ServiceMessages.WRONG_PASSPORT.getMessage());
                    passport = null; // повторить ввод
                }
            } catch (EnteredNotValidDataException e) {
                throw new RuntimeException(e);
            }
        }
        return passport;
    }

    private String getPassport(String passport, int id) {
        while (passport == null) {
            passport = getValidInput(ServiceMessages.ENTER_PASSPORT.getMessage(),
                    ServiceMessages.ERROR_MESSAGE_EMPTY_PASSPORT.getMessage());

            if (passport.equalsIgnoreCase("exit")) {
                System.out.println("Введите help для получения помощи");
                return null;
            }

            // Валидация паспорта
            if (!validatePassportNumber(passport, id)) {
                System.out.println(ServiceMessages.WRONG_PASSPORT.getMessage());
                passport = null; // повторить ввод
            }
        }
        return passport;
    }

    private String getAddress(String address) {
        while (address == null) {
            address = getValidInput(ServiceMessages.ENTER_ADDRESS.getMessage(),
                    ServiceMessages.ERROR_MESSAGE_EMPTY_ADDRESS.getMessage());

            if (address.equalsIgnoreCase("exit")) {
                System.out.println("Введите help для получения помощи");
                return null;
            }

//            // Валидация адреса
//            if (validateAddress(address)) {
//                System.out.println(ServiceMessages.WRONG_ADDRESS.getMessage());
//                address = null; // повторить ввод
//            }
        }
        return address;
    }


    public void checkIn() {
//        try {
//            int idGuest = selectGuestToCheckIn();
//            if (validateCheckInGuest(idGuest)) {
//
//                try (Socket socket = new Socket("localhost", 12345);
//                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//                    int idHotel = selectHotelToCheckIn(in, out);
//                    processRoomAvailability(in, idGuest, idHotel);
//
//                } catch (IOException e) {
//                    System.out.println("Сервис отелей недоступен");
//                    monitoringSocketClient.sendEvent(EventType.MISTAKE, "Сервис отелей недоступен");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Неизвестная ошибка при заселении");
//        }
            return;
    }





    public void bookRoom(GuestEntity guestId, int hotelId) {
        BookingEntity booking = new BookingEntity(guestId, hotelId);
        bookingRepository.save(booking);
        System.out.println(ServiceMessages.CHECK_IN_SUCCESS.getMessage(guestId, hotelId));
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



    public boolean validateCreateGuest(GuestEntity guestEntity) throws IOException, EnteredNotValidDataException {
        if (validateName(guestEntity.getName())) {
            System.out.println(ServiceMessages.WRONG_NAME.getMessage());
            return false;
        }
        if (validateAge(guestEntity.getAge())) {
            System.out.println(ServiceMessages.WRONG_AGE.getMessage());
            return false;
        }

        if (validateAddress(guestEntity.getAddress())) {
            System.out.println(ServiceMessages.WRONG_ADDRESS.getMessage());
            return false;
        }

        if (validatePassportNumber(guestEntity.getPassportNumber())) {
            System.out.println(ServiceMessages.WRONG_PASSPORT.getMessage());
            return false;
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
            throw new EnteredNotValidDataException(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
        }

        return null;
    }


    private boolean validateName(String name) throws EnteredNotValidDataException {
        if(name == null || name.trim().isEmpty()){
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_EMPTY_NAME.getMessage());
        }
        if(name.length() > 20){
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_NAME.getMessage());
        }

        if(!Character.isUpperCase(name.charAt(0))){
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

    private String getValidInput(String prompt, String errorMessage) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(errorMessage);
            }
        } while (input.isEmpty());
        return input;
    }


    private boolean validatePassportNumber(String passportNumber) throws EnteredNotValidDataException {
        if (passportNumber != null && passportNumber.length() == 6 && passportNumber.matches("\\d{6}")) {
            boolean passportExists = true;
            List<GuestEntity> guests = guestRepository.findAll();
            for (GuestEntity guest : guests) {
                if (guest.getPassportNumber().equals(passportNumber)) {
                    throw new EnteredNotValidDataException(ServiceMessages.EXISTING_GUEST.getMessage());
                }
            }
            return !passportExists;
        } else {
            throw new EnteredNotValidDataException(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
        }
    }

    private boolean validatePassportNumber(String passportNumber, int id) {
        if (passportNumber != null && passportNumber.length() == 6 && passportNumber.matches("\\d{6}")) {
            boolean passportExists = guestRepository.existsByPassportNumberAndIdNot(passportNumber, id);
            if (passportExists) {
                System.out.println(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
                return false;
            }
            return true;
        } else {
            System.out.println(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
            return false;
        }
    }


    private boolean validateAddress(String address) throws EnteredNotValidDataException {
        if(address == null || address.trim().isEmpty()){
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_EMPTY_ADDRESS.getMessage());
        }
        if(address.length() > 20){
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_LENGTH_ADDRESS.getMessage());
        }

        if(!Character.isUpperCase(address.charAt(0))){
            throw new EnteredNotValidDataException(ServiceMessages.ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_ADDRESS.getMessage());
        }

        return address == null || address.length() > 30 || !Character.isUpperCase(address.charAt(0));
    }

    public void readGuest() {
        String idInput = getValidInput(ServiceMessages.ENTER_ID.getMessage(),
                ServiceMessages.ERROR_MESSAGE_EMPTY_ID.getMessage());
        int id = Integer.parseInt(idInput);
        if (guestRepository.existsById(id)) {
            System.out.println(guestRepository.findById(id));
        } else {
            System.out.println(ServiceMessages.WRONG_GUEST_ID.getMessage());
        }

    }

    public void deleteGuestByDB() {
        String idInput = getValidInput(ServiceMessages.ENTER_ID.getMessage(),
                ServiceMessages.ERROR_MESSAGE_EMPTY_ID.getMessage());
        int id = Integer.parseInt(idInput);
        if (guestRepository.existsById(id)) {
            guestRepository.deleteById(id);
        } else {
            System.out.println(ServiceMessages.WRONG_GUEST_ID.getMessage());
        }
    }

    public void updateGuest() throws EnteredNotValidDataException {
        String idInput = getValidInput(ServiceMessages.ENTER_ID.getMessage(),
                ServiceMessages.ERROR_MESSAGE_EMPTY_ID.getMessage());
        int id = Integer.parseInt(idInput);

        if (guestRepository.existsById(id)) {

            System.out.println(guestRepository.findById(id));
            String firstName = null;
            int age = 0;
            String address = null;
            String passport = null;

            System.out.println("Если вы хотите закончить ввод - введите exit");

            firstName = getFirstName(firstName);
            if (firstName == null) return;  // Прерывание метода

            // Ввод возраста с повторной попыткой в случае ошибки
            while (age == 0) {
                try {
                    String ageInput = getValidInput(ServiceMessages.ENTER_AGE.getMessage(),
                            ServiceMessages.ERROR_MESSAGE_EMPTY_AGE.getMessage());
                    if (ageInput.equalsIgnoreCase("exit")) {
                        System.out.println("Введите help для получения помощи");
                        return;  // Прерывание метода
                    }
                    age = Integer.parseInt(ageInput);


                    // Валидация возраста
                    if (validateAge(age)) {
                        System.out.println(ServiceMessages.WRONG_AGE.getMessage());
                        age = 0; // повторить ввод
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ServiceMessages.ERROR_AGE_NOT_INT.getMessage());
                    age = 0; // повторить ввод
                }
            }

            // Ввод адреса с повторной попыткой в случае ошибки
            address = getAddress(address);
            if (address == null) return;  // Прерывание метода

            // Ввод паспорта с повторной попыткой в случае ошибки
            passport = getPassport(passport, id);
            if (passport == null) return;  // Прерывание метода

            // Создание DTO и сущности после успешного ввода всех данных
            GuestEntity guest = new GuestEntity(firstName, age, passport, address);
            guest.setId(id);
            guestRepository.save(guest);
        } else {
            System.out.println("Гостя с таким ID не существует");
        }
    }

    public void readGuests() {
        List<GuestEntity> guests = guestRepository.findAllByOrderByIdAsc();
        for (GuestEntity guest : guests) {
            guest.getInfo();
        }
    }

    public List<GuestEntity> getAllGuests() {
        return guestRepository.findAllByOrderByIdAsc();
    }

    public GuestEntity getGuestByID(int id) {
        if (guestRepository.existsById(id)) {
            return guestRepository.findById(id).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }
    }

    public void addGuest(GuestEntity guestEntity) throws EnteredNotValidDataException {
        String validationError = validateCreateGuestHttp(guestEntity);
        if (validationError != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationError);
        }
        guestRepository.save(guestEntity);
    }

    public void deleteGuestHttp(int id) {
        if (guestRepository.existsById(id)) {
            guestRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }
    }

    public int getHotelId(int guestId) {
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_NOT_FOUND.getMessage(guestId));
        }
    }


    public void updateGuestHttp(int id, GuestEntity guest) throws EnteredNotValidDataException {
        if (guestRepository.existsById(id)) {
            String validationError = validateUpdateGuestHttp(guest);
            if (validationError != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationError);
            }
            boolean passportExists = guestRepository.existsByPassportNumberAndIdNot(guest.getPassportNumber(), id);
            if (passportExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, ServiceMessages.GUEST_WITH_PASSPORT_EXIST.getMessage(guest.getPassportNumber()));
            } else {
                guest.setId(id);
                guestRepository.save(guest);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_NOT_FOUND.getMessage(id));
        }
    }

    public ResponseEntity<String> validateCheckInHttp(int guestId, int hotelId) {

        // Проверяем, существует ли гость
        if (!validateCheckInGuest(guestId)) {
            return ResponseEntity.badRequest().body(ServiceMessages.WRONG_GUEST_ID.getMessage());
        }

        // Проверяем, что гость еще не заселен
        if (!validateBookingDoubleCheckIn(guestId)) {
            return ResponseEntity.badRequest().body(ServiceMessages.EXIST_CHECKIIN.getMessage());
        }

        return ResponseEntity.ok(ServiceMessages.ACCESS_CHECKIN.getMessage());
    }

    public void sendBookingToMonitoring(EventType eventType, String message) {
        MonitoringEvent event = new MonitoringEvent(eventType, message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BOOKING_EXCHANGE_NAME, RabbitMQConfig.MONITORING_ROUTING_KEY, event);
    }
}
