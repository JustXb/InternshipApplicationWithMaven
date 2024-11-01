package com.example.monitoringsystem.controller;

import com.example.MonitoringEvent;
import com.example.monitoringsystem.service.MonitoringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("log")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @PostMapping("/logEvent")
    public ResponseEntity<String> logEvent(@RequestBody MonitoringEvent event) {
        try {
            // Логируем событие через сервис мониторинга
            monitoringService.logEvent(event.getEventType(), event.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body("Событие успешно зарегистрировано");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке события");
        }
    }
}
