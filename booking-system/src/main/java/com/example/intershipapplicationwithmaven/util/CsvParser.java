package com.example.intershipapplicationwithmaven.util;

import com.example.intershipapplicationwithmaven.repository.entity.BookingEntity;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class CsvParser {

    private final String guestsFilePath;
    private final String bookingsFilePath;
    private final String PATTERN_GUEST_CSV = "ID,Name,Age,PassportNumber,Address";
    private final String PATTERN_BOOKINGS_CSV = "ID, GuestID, HotelID";
    private final Logger LOGGER = Logger.getLogger(CsvParser.class.getName());

    public CsvParser(
            @Value("${receiver.guests.csv.filepath}") String guestsFilePath,
            @Value("${receiver.bookings.csv.filepath}") String bookingsFilePath) {
        this.guestsFilePath = guestsFilePath;
        this.bookingsFilePath = bookingsFilePath;
    }

    public void saveGuests(List<GuestEntity> guests) throws IOException {
        checkIfFileExist(guestsFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(guestsFilePath))) {
            writer.write(PATTERN_GUEST_CSV);
            writer.newLine();
            for (GuestEntity guest : guests) {
                writer.write(guest.getId() + "," + guest.getName() + "," + guest.getAge() + "," +
                        guest.getPassportNumber() + "," + guest.getAddress());
                writer.newLine();
            }
            LOGGER.info("Данные успешно записаны в " + guestsFilePath);
        }
    }

    public List<GuestEntity> loadGuests() throws IOException {
        checkIfFileExist(guestsFilePath);
        List<GuestEntity> guests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(guestsFilePath))) {
            reader.readLine(); // Пропускаем заголовок
            String dataline;
            while ((dataline = reader.readLine()) != null) {
                String[] fields = dataline.split(",");
                if (fields.length == 5) {
                    guests.add(new GuestEntity(Integer.parseInt(fields[0]), fields[1], Integer.parseInt(fields[2]),
                            fields[3], fields[4]));
                }
            }
        }
        return guests;
    }

    public void saveBookings(List<BookingEntity> bookingEntities) throws IOException {
        checkIfFileExist(bookingsFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookingsFilePath))) {
            writer.write(PATTERN_BOOKINGS_CSV);
            writer.newLine();
            for (BookingEntity bookingEntity : bookingEntities) {
                writer.write(bookingEntity.getId() + "," + bookingEntity.getGuestId() + "," + bookingEntity.getHotelId());
                writer.newLine();
            }
            LOGGER.info("Данные успешно записаны в " + bookingsFilePath);
        }
    }

    public List<BookingEntity> loadBookings() throws IOException {
        checkIfFileExist(bookingsFilePath);
        List<BookingEntity> bookingEntities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(bookingsFilePath))) {
            reader.readLine(); // Пропускаем заголовок
            String dataline;
            while ((dataline = reader.readLine()) != null) {
                String[] fields = dataline.split(",");
                if (fields.length == 3) {
                    bookingEntities.add(new BookingEntity(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]),
                            Integer.parseInt(fields[2])));
                }
            }
        }
        return bookingEntities;
    }

    private void checkIfFileExist(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}

