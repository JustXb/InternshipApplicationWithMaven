package com.example.hotelsystem.service;

public enum ServiceMessages {
    WAITING_CONNECT("Сервис гостиницы запущен и ожидает подключения..."),
    REQUEST_HOTEL_AVAILABILITY("Запрос на доступность гостиницы: "),
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE"),
    UNAVAILABLE_NOAVAILABILITY("UNAVAILABLE_NOAVAILABILITY"),
    WRONG_HOTEL("Такого отеля не существует");



    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
