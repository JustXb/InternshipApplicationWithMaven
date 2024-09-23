package com.example.intershipapplicationwithmaven.repository.impl;

import com.example.intershipapplicationwithmaven.repository.AbstractRepository;
import com.example.intershipapplicationwithmaven.repository.entity.BookingEntity;
import com.example.intershipapplicationwithmaven.util.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class BookingCsvRepository extends AbstractRepository<BookingEntity> {

    @Autowired
    public BookingCsvRepository(CsvParser csvParser) {
        super(csvParser);
    }

    @Override
    public void create(BookingEntity bookingEntity) throws IOException {
        List<BookingEntity> bookings = csvParser.loadBookings();
        int id = bookings.size();
        bookingEntity.setId(++id);
        bookings.add(bookingEntity);
        csvParser.saveBookings(bookings);
    }

    @Override
    protected String getCsvFilePath() {
        return "${receiver.bookings.csv.filepath}"; // Замените на фактический путь
    }
}
