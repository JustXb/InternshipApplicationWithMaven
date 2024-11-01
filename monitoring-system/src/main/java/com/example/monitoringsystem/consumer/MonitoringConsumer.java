package com.example.monitoringsystem.consumer;


import com.example.MonitoringEvent;
import com.example.monitoringsystem.service.MonitoringService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MonitoringConsumer {

    private final MonitoringService monitoringService;

    public MonitoringConsumer(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @RabbitListener(queues = "bookingQueue")
    public void receiveMessage(MonitoringEvent event) {
        monitoringService.logEvent(event.getEventType(), event.getMessage());
        System.out.println("Получено сообщение: " + event.getMessage());
    }

}

