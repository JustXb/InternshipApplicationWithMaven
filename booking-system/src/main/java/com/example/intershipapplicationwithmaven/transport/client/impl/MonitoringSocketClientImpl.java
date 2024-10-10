package com.example.intershipapplicationwithmaven.transport.client.impl;

import com.example.EventType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class MonitoringSocketClientImpl {

    @Value("${monitoring.server.host}")
    private String host;

    @Value("${monitoring.server.port}")
    private int port;

    public void sendEvent(EventType eventType, String message) {
        try (Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(eventType + ";" + message);
        } catch (IOException e) {
            System.out.println("Сервис мониторинга недоступен");
        }
    }
}
