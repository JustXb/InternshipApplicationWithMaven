package com.example.monitoringsystem.service;

import com.example.EventType;
import com.example.monitoringsystem.repository.FileWriter;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MonitoringService {

    private final FileWriter fileWriter;
    private static final String RECORD_SEPARATOR = "\n";


    public MonitoringService(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void logEvent(EventType eventType, String message) {
        String timestamp = new Date().toString().replace(":", "-").replace(" ", "_");
        String logEntry = timestamp + ' ' + eventType + ' ' + message + RECORD_SEPARATOR;
        fileWriter.writeLogEntry(logEntry);
    }


}
