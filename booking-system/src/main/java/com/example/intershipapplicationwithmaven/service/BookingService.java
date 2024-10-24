package com.example.intershipapplicationwithmaven.service;


import com.example.EventType;
import com.example.MonitoringEvent;
import com.example.intershipapplicationwithmaven.config.RabbitMQConfig;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

    public void createGuest() {
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
                if (!validateAge(age)) {
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
                //monitoringSocketClient.sendEvent(EventType.CREATED, "Гость добавлен: " + guest.toString());
            } else {
                //monitoringSocketClient.sendEvent(EventType.MISTAKE, "Добавление гостя " + guest.toString() + " неудачно");
            }
        } catch (IOException e) {
            System.out.println(ServiceMessages.ERROR_CREATE_GUEST.getMessage());
        } catch (Exception e) {
            LOGGER.severe(ServiceMessages.UNKNOWN_ERROR.getMessage() + e.getMessage());
        }
    }

    private String getFirstName(String firstName) {
        // Ввод имени с повторной попыткой в случае ошибки
        while (firstName == null) {
            firstName = getValidInput(ServiceMessages.ENTER_NAME.getMessage(),
                    ServiceMessages.ERROR_MESSAGE_EMPTY_NAME.getMessage());

            if (firstName.equalsIgnoreCase("exit")) {
                System.out.println("Введите help для получения помощи");
                return null;
            }

            // Валидация имени
            if (!validateName(firstName)) {
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
                if (!validatePassportNumber(passport)) {
                    System.out.println(ServiceMessages.WRONG_PASSPORT.getMessage());
                    passport = null; // повторить ввод
                }
            } catch (IOException e) {
                System.out.println(ServiceMessages.ERROR_CREATE_GUEST.getMessage() + e.getMessage());
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

            // Валидация адреса
            if (!validateAddress(address)) {
                System.out.println(ServiceMessages.WRONG_ADDRESS.getMessage());
                address = null; // повторить ввод
            }
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

//    private int selectGuestToCheckIn() {
//        List<GuestEntity> guestEntities = guestRepository.findAll();
//        for (GuestEntity guest : guestEntities) {
//            guest.getInfo();
//        }
//        System.out.println(ServiceMessages.SELECT_GUEST.getMessage());
//        String input;
//        int guestId = -1;
//
//        while (true) {
//            input = scanner.nextLine().trim(); // Убираем пробелы
//
//            // Проверяем на пустую строку
//            if (input.isEmpty()) {
//                System.out.println("Ошибка: Ввод не может быть пустым. Повторите ввод.");
//                continue;
//            }
//
//            try {
//                guestId = Integer.parseInt(input);
//                break;
//            } catch (NumberFormatException e) {
//                System.out.println("Ошибка: Введите корректный числовой идентификатор гостя.");
//            }
//        }
//
//        return guestId;
//    }
//
//    private void processRoomAvailability(BufferedReader in, int idGuest, int idHotel) throws IOException {
//        String getAvailabilityResponse = in.readLine(); // Получение ответа
//        if ("AVAILABLE".equals(getAvailabilityResponse)) {
//            bookRoom(idGuest, idHotel);
//        } else {
//            if ("UNAVAILABLE".equals(getAvailabilityResponse)) {
//                System.out.println("Такого отеля не существует");
//                monitoringSocketClient.sendEvent(EventType.MISTAKE, "Гостиница " + idHotel +
//                        " недоступна. Заявка отменена.");
//            } else {
//                System.out.println("В этом отеле нет мест");
//                monitoringSocketClient.sendEvent(EventType.MISTAKE, "Гостиница " + idHotel +
//                        " недоступна. Заявка отменена.");
//            }
//        }
//    }

    private int selectHotelToCheckIn(BufferedReader in, PrintWriter out) throws IOException {
        String getAllHotelsResponse = in.readLine();
        System.out.println(getAllHotelsResponse);
        int idHotel = 0;
        while (idHotel == 0) {
            try {
                String ageInput = getValidInput(ServiceMessages.ENTER_HOTEL.getMessage(),
                        ServiceMessages.ERROR_MESSAGE_EMPTY_ID.getMessage());

                idHotel = Integer.parseInt(ageInput);

                out.println(idHotel);
                return idHotel;
            } catch (NumberFormatException e) {
                System.out.println(ServiceMessages.ERROR_MESSAGE_ID_NOT_INT.getMessage());
                idHotel = 0; // повторить ввод
            }
        }
        return idHotel;
    }

    public void bookRoom(GuestEntity guestId, int hotelId) {
        BookingEntity booking = new BookingEntity(guestId, hotelId);
        bookingRepository.save(booking);
        System.out.println("Гость " + guestId + " успешно заселен в отель " + hotelId);
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

//    public void bookRoom(int guestId, int hotelId) {
//        if (validateBookingDoubleCheckIn(guestId)) {
//            BookingEntity booking = new BookingEntity(guestId, hotelId);
//            bookingRepository.save(booking);
//            System.out.println("Гость " + guestId + " успешно заселен в " + hotelId);
//            monitoringSocketClient.sendEvent(EventType.CREATED, "Гость " + guestId +
//                    " заселен в отель " + hotelId);
//        }
//    }
//
//
//    public boolean validateBookingDoubleCheckIn(int guestId) {
//        List<BookingEntity> bookings = bookingRepository.findAll();
//        for (BookingEntity booking : bookings) {
//            if (booking.getGuestId() == guestId) {
//                System.out.println("Заселение отменено: гость уже заселен в отель " + booking.getHotelId());
//                monitoringSocketClient.sendEvent(EventType.MISTAKE, "Заселение отменено: гость уже заселен в" +
//                        " отель " + booking.getHotelId());
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean validateCheckInGuest(int guestId) {
//        boolean isExist = false;
//        isExist = guestRepository.existsById(guestId);
//
//        if (!isExist) {
//            System.out.println("Заселение отменено: гостя с таким ID не существует");
//            monitoringSocketClient.sendEvent(EventType.MISTAKE, "Заселение отменено: гостя с таким ID не существует");
//        }
//        return isExist;
//    }

    public boolean validateCreateGuest(GuestEntity guestEntity) throws IOException {
        if (!validateName(guestEntity.getName())) {
            System.out.println(ServiceMessages.WRONG_NAME.getMessage());
            return false;
        }
        if (!validateAge(guestEntity.getAge())) {
            System.out.println(ServiceMessages.WRONG_AGE.getMessage());
            return false;
        }

        if (!validateAddress(guestEntity.getAddress())) {
            System.out.println(ServiceMessages.WRONG_ADDRESS.getMessage());
            return false;
        }

        if (!validatePassportNumber(guestEntity.getPassportNumber())) {
            System.out.println(ServiceMessages.WRONG_PASSPORT.getMessage());
            return false;
        }
        return true;
    }

    public String validateCreateGuestHttp(GuestEntity guestEntity) throws IOException {
        if (!validateName(guestEntity.getName())) {
            return ServiceMessages.WRONG_NAME.getMessage();
        }
        if (!validateAge(guestEntity.getAge())) {
            return ServiceMessages.WRONG_AGE.getMessage();
        }
        if (!validateAddress(guestEntity.getAddress())) {
            return ServiceMessages.WRONG_ADDRESS.getMessage();
        }
        if (!validatePassportNumber(guestEntity.getPassportNumber())) {
            return ServiceMessages.WRONG_PASSPORT.getMessage();
        }
        return null;  // Валидация прошла успешно
    }


    private boolean validateName(String name) {
        return name != null && name.length() <= 20 && Character.isUpperCase(name.charAt(0));
    }

    private boolean validateAge(int age) {
        return age >= 0 && age <= 120;
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


    private boolean validatePassportNumber(String passportNumber) throws IOException {
        if (passportNumber != null && passportNumber.length() == 6 && passportNumber.matches("\\d{6}")) {
            boolean passportExists = true;
            List<GuestEntity> guests = guestRepository.findAll();
            for (GuestEntity guest : guests) {
                if (guest.getPassportNumber().equals(passportNumber)) {
                    System.out.println(ServiceMessages.EXISTING_GUEST.getMessage());
                    passportExists = false;
                    break;
                }
            }
            return passportExists;
        } else {
            System.out.println(ServiceMessages.WRONG_COUNT_NUMBER_PASSPORT.getMessage());
            return false;
        }
    }

    private boolean validatePassportNumber(String passportNumber, int id) {
        if (passportNumber != null && passportNumber.length() == 6 && passportNumber.matches("\\d{6}")) {
            // Используем метод репозитория для проверки номера паспорта
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


    private boolean validateAddress(String address) {
        return address != null && address.length() <= 30 && Character.isUpperCase(address.charAt(0));
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

    public void updateGuest() {
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
                    if (!validateAge(age)) {
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
        return guestRepository.findById(id).get();
    }

    public void addGuest(GuestEntity guestEntity) throws IOException {
        String validationError = validateCreateGuestHttp(guestEntity);
        if (validationError != null) {
            // Если валидация не прошла, бросаем исключение с конкретным сообщением ошибки
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationError);
        }

        // Если валидация прошла успешно, сохраняем гостя
        guestRepository.save(guestEntity);
    }

    public void deleteGuestHttp(int id) {
        if (guestRepository.existsById(id)) {
            guestRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_WITH_ID.getMessage() + id +
                    ServiceMessages.NOT_FOUND.getMessage());
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUEST_WITH_ID.getMessage()
                    + guestId + ServiceMessages.NOT_FOUND.getMessage());
        }
    }


    public void updateGuestHttp(int id, GuestEntity guest) {
        if (guestRepository.existsById(id)) {
            boolean passportExists = guestRepository.existsByPassportNumberAndIdNot(guest.getPassportNumber(), id);
            if (passportExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, ServiceMessages.GUEST_WITH_PASSPORT.getMessage() +
                        guest.getPassportNumber() + ServiceMessages.EXIST.getMessage());
            } else {
                guest.setId(id);
                guestRepository.save(guest);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ServiceMessages.GUESTs_WITH_ID.getMessage() + id +
                    ServiceMessages.NOT_FOUND.getMessage());
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

        return ResponseEntity.ok(ServiceMessages.ACCESS_CHECKIN.getMessage()); // Если все проверки пройдены
    }


    public void sendEvent(EventType eventType, String message) {
        try {
            // Формируем тело запроса
            MonitoringEvent event = new MonitoringEvent(eventType, message);
            // Отправляем POST-запрос на сервис мониторинга
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8082/log" + "/logEvent", event, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Событие успешно отправлено");
            } else {
                System.out.println("Ошибка при отправке события: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Не удалось подключиться к сервису мониторинга: " + e.getMessage());
        }
    }



    public void sendBookingToMonitoring(EventType eventType, String message) {
        MonitoringEvent event = new MonitoringEvent(eventType, message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BOOKING_EXCHANGE_NAME, RabbitMQConfig.MONITORING_ROUTING_KEY, event);
    }
}
