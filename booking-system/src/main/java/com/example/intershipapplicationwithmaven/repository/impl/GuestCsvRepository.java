package com.example.intershipapplicationwithmaven.repository.impl;

import com.example.intershipapplicationwithmaven.repository.AbstractRepository;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.util.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public Optional<GuestEntity> read(int id) throws IOException {
        List<GuestEntity> guests = csvParser.loadGuests();
        return guests.stream().filter(guest -> guest.getId() == id).findFirst();
    }

    public void update(GuestEntity guestEntity) throws IOException {
        List<GuestEntity> guests = csvParser.loadGuests();
        for (int i = 0; i < guests.size(); i++) {
            if (guests.get(i).getId() == guestEntity.getId()) {
                guests.set(i, guestEntity);
                break;
            }
        }
        csvParser.saveGuests(guests);
    }

    public void delete(int id) throws IOException {
        List<GuestEntity> guests = csvParser.loadGuests();
        guests.removeIf(guest -> guest.getId() == id);
        csvParser.saveGuests(guests);
    }

    @Override
    protected String getCsvFilePath() {
        return "${receiver.guests.csv.filepath}"; // Замените на фактический путь
    }
}
