package com.example.monitoringsystem.service;

import com.example.EventType;
import com.example.MonitoringEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MonitoringService {
    private static final String LOG_FILE_PREFIX = "monitoring_";
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private static final String RECORD_SEPARATOR = "\n";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Value("${LOG_DIRECTORY:logs}")
    private String logDirectory;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(logDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для логов", e);
        }
    }

    public void logEvent(String eventType, String message) {
        String header = createHeader(eventType);
        String timestamp = new Date().toString().replace(":", "-").replace(" ", "_");
        String logEntry = timestamp + ' ' + header + ' ' + message + RECORD_SEPARATOR;
        writeLogEntry(logEntry);
    }

    private String createHeader(String eventType) {
        switch (EventType.valueOf(eventType)) {
            case CREATED: return "CREATED";
            case MISTAKE: return "MISTAKE";
            case SUCCESS: return "SUCCESS";
            default: throw new IllegalArgumentException("Неверный тип события: " + eventType);
        }
    }

    private void writeLogEntry(String logEntry) {
        try {
            File logFile = getCurrentLogFile();
            if (logFile.length() >= MAX_FILE_SIZE) {
                rotateLogFile(logFile);
            }
            try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                fos.write(logEntry.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getCurrentLogFile() {
        String currentDate = DATE_FORMAT.format(new Date());
        return new File(logDirectory, LOG_FILE_PREFIX + currentDate + ".bin");
    }

    private void rotateLogFile(File logFile) throws IOException {
        File newLogFile = new File(logDirectory, "monitoring.bin");
        Files.move(logFile.toPath(), newLogFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @RabbitListener(queues = "bookingQueue")
    public void receiveMessage(MonitoringEvent event) {
        System.out.println("Получено сообщение: " + event);
        logEvent(event.getEventType().toString(), event.getMessage());
    }
}
