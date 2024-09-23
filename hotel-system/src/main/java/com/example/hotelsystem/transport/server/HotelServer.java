package com.example.hotelsystem.transport.server;

import com.example.hotelsystem.repository.impl.HotelJsonRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class HotelServer {

    private final HotelJsonRepository hotelJsonRepository;

    public HotelServer(HotelJsonRepository hotelJsonRepository) {
        this.hotelJsonRepository = hotelJsonRepository;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Сервис гостиницы запущен и ожидает подключения...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    // Обработка подключения клиента
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
