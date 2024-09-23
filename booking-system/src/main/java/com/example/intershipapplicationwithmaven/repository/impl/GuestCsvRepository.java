package com.example.intershipapplicationwithmaven.repository.impl;

import com.example.intershipapplicationwithmaven.repository.AbstractRepository;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.util.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class GuestCsvRepository extends AbstractRepository<GuestEntity> {

    @Autowired
    public GuestCsvRepository(CsvParser csvParser) {
        super(csvParser);
    }

    @Override
    public void create(GuestEntity guestEntity) throws IOException {
        List<GuestEntity> guests = csvParser.loadGuests();
        int id = guests.size();
        guestEntity.setId(++id);
        guests.add(guestEntity);
        csvParser.saveGuests(guests);
    }

    @Override
    protected String getCsvFilePath() {
        return "${receiver.guests.csv.filepath}"; // Замените на фактический путь
    }
}
