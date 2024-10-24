package com.example;

import java.io.Serializable;

public class MonitoringEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private EventType eventType;
    private String message;

    public MonitoringEvent(EventType eventType, String message) {
        this.eventType = eventType;
        this.message = message;
    }

    // Геттеры и сеттеры
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
