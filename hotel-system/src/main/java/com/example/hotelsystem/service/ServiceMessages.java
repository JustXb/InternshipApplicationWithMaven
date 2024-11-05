package com.example.hotelsystem.service;

public enum ServiceMessages {
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE"),
    LACK_OF_PLACES("UNAVAILABLE_NOAVAILABILITY"),
    WRONG_HOTEL("Такого отеля не существует"),
    INCREASE_HOTEL_AVAILABILITY("Доступность мест в отеле увеличена на 1");


    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
