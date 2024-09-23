package com.example.intershipapplicationwithmaven.repository;

import com.example.intershipapplicationwithmaven.repository.entity.Entity;
import com.example.intershipapplicationwithmaven.util.CsvParser;
import com.example.intershipapplicationwithmaven.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public abstract class AbstractRepository<E extends Entity> {

    protected final CsvParser csvParser;
    protected final Mapper mapper;

    @Autowired
    public AbstractRepository(CsvParser csvParser) {
        this.csvParser = csvParser;
        this.mapper = new Mapper();
    }

    public abstract void create(E entity) throws IOException;

    protected abstract String getCsvFilePath(); // Метод для получения пути к файлу

    protected void checkFilePath() {
        // Здесь можно добавить проверку пути к файлу, если это необходимо
    }
}
