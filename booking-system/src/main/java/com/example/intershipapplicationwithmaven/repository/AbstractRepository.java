package com.example.intershipapplicationwithmaven.repository;

import com.example.intershipapplicationwithmaven.repository.entity.Entity;
import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import com.example.intershipapplicationwithmaven.util.CsvParser;
import com.example.intershipapplicationwithmaven.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

@Repository
public abstract class AbstractRepository<E extends Entity> {

    protected final CsvParser csvParser;
    protected final Mapper mapper;

    @Autowired
    public AbstractRepository(CsvParser csvParser) {
        this.csvParser = csvParser;
        this.mapper = new Mapper();
        checkFilePath(); // Проверка пути при инициализации
    }

    public abstract void create(E entity) throws IOException;

    public abstract Optional<E> read(int id) throws IOException;
    public abstract void update(E entity) throws IOException; // Метод для обновления сущности

    public abstract void delete(int id) throws IOException; // Метод для удаления сущности

    protected abstract String getCsvFilePath(); // Метод для получения пути к файлу

    protected void checkFilePath() {
        // Здесь можно добавить проверку пути к файлу
        String filePath = getCsvFilePath();
        // Реализуйте логику проверки
    }
}
