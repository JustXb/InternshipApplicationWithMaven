package com.example;

public class MonitoringEvent {
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
