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
    ERROR_MESSAGE_ID_NOT_INT("Поле ID должно быть числом"),
    UNKNOWN_ERROR("Неизвестная ошибка: "),
    REQUEST_TO_HOTEL_SYSTEM("Реквест к сервису отелей"),
    EXISTING_GUEST("Гость с такими паспортными данными уже существует"),
    WRONG_GUEST_ID("Гостя с таким ID не существует"),
    GUEST_WITH_ID("Гость с ID "),
    GUESTs_WITH_ID("Гостя с ID "),
    NOT_FOUND(" не найден."),
    EXIST(" уже существует."),
    GUEST_WITH_PASSPORT("Гость с паспортными данными "),
    EXIST_CHECKIIN("Гость уже заселен в другой отель."),
    ACCESS_CHECKIN("Гость может быть заселен.");






    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
