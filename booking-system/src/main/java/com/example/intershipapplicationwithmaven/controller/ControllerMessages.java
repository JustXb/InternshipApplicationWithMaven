package com.example.intershipapplicationwithmaven.controller;

public enum ControllerMessages {
    GUEST_ADDED("Гость успешно добавлен: "),
    GUEST_UPDATED("Гость с ID %d успешно обновлен."),
    GUEST_DELETED("Гость с ID %d успешно удален, доступность отеля %d увеличена."),
    GUEST_NOT_FOUND("Гостя с ID %d не существует."),
    HOTEL_SERVICE_UNAVAILABLE("Сервис отелей недоступен."),
    CHECK_IN_SUCCESS("Гость %d успешно заселен в отель %d."),
    CHECK_IN_ERROR("Ошибка заселения: %s"),
    GENERAL_ERROR("Ошибка: %s");

    private final String message;

    ControllerMessages(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}

