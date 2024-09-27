package com.example.intershipapplicationwithmaven.service;

public enum ServiceMessages {
    ENTER_NAME("Enter Name :"),
    ENTER_AGE("Enter age :"),
    ENTER_ADDRESS("Enter Address :"),
    ENTER_PASSPORT("Enter passport number :"),
    ENTER_HOTEL("Enter hotel :"),
    WRONG_NAME("Неверное имя пользователя"),
    WRONG_AGE("Неверно указан возраст"),
    WRONG_ADDRESS("Неверно указан адрес"),
    WRONG_PASSPORT("Номер паспорта не валиден"),
    WRONG_COUNT_NUMBER_PASSPORT("Номер паспорта должен содержать ровно 6 цифр."),
    SELECT_GUEST("Выберите гостя по его ID"),
    ERROR_MESSAGE_EMPTY_NAME("имя не может быть пустым."),
    ERROR_MESSAGE_EMPTY_AGE("Возраст не может быть пустым."),
    ERROR_MESSAGE_EMPTY_ADDRESS("Адрес не может быть пустым."),
    ERROR_MESSAGE_EMPTY_PASSPORT("Номер паспорта не может быть пустым."),
    ERROR_AGE_NOT_INT("Ошибка: возраст должен быть числом."),
    ERROR_CREATE_GUEST("Ошибка при создании гостя: " ),

    ENTER_ID("Введите ID гостя: "),

    ERROR_MESSAGE_EMPTY_ID("Поле ID не может быть пустым"),

    UNKNOWN_ERROR("Неизвестная ошибка: "),
    REQUEST_TO_HOTEL_SYSTEM("Реквест к сервису отелей");




    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
