package com.internshipbooking.producer;

import com.example.EventType;
import com.example.MonitoringEvent;
import com.internshipbooking.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingProducer {

    private final RabbitTemplate rabbitTemplate;

    public BookingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookingToMonitoring(EventType eventType, String message) {
        MonitoringEvent event = new MonitoringEvent(eventType, message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BOOKING_EXCHANGE_NAME,
                RabbitMQConfig.MONITORING_ROUTING_KEY,
                event
        );
    }
}
