package com.example.monitoringsystem.transport.server;

import com.example.monitoringsystem.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

@Component
public class MonitoringServer {
    private final int PORT = 5555; // Порт для прослушивания
    private final MonitoringService monitoringService;
    private final Logger LOGGER = Logger.getLogger(MonitoringServer.class.getName());

    @Autowired
    public MonitoringServer(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Сервер мониторинга запущен и ожидает подключения...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String[] parts = inputLine.split(";", 2);
                        if (parts.length == 2) {
                            String eventType = parts[0];
                            String message = parts[1];
                            monitoringService.logEvent(eventType, message);
                            LOGGER.info("Добавлен новый лог");
                        }
                    }
                } catch (IOException e) {
                    LOGGER.warning("Сервер мониторинга недоступен");
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
