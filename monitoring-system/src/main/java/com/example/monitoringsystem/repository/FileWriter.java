package com.example.monitoringsystem.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class FileWriter {
    private static final String LOG_FILE_PREFIX = "monitoring_";
    private static final long MAX_FILE_SIZE = 1024 * 1024;
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


    public void writeLogEntry(String logEntry) {
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
}
