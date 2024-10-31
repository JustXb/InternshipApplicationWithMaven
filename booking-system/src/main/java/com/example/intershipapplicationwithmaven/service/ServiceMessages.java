package com.example.intershipapplicationwithmaven.service;

public enum ServiceMessages {
    ENTER_NAME("Enter Name :"),
    ENTER_AGE("Enter age :"),
    ENTER_ADDRESS("Enter Address :"),
    ENTER_PASSPORT("Enter passport number :"),
    ENTER_HOTEL("Enter hotel :"),
    WRONG_NAME("Неверное имя гостя"),
    WRONG_AGE("Неверно указан возраст"),
    WRONG_ADDRESS("Неверно указан адрес"),
    WRONG_PASSPORT("Номер паспорта не валиден"),
    WRONG_COUNT_NUMBER_PASSPORT("Номер паспорта должен содержать ровно 6 цифр."),
    ERROR_MESSAGE_EMPTY_NAME("имя не может быть пустым."),
    ERROR_MESSAGE_WRONG_LENGTH_NAME("Имя гостя не может быть длиннее 20 символов"),
    ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_NAME("Имя гостя должно начинаться с большой буквы"),
    ERROR_MESSAGE_EMPTY_AGE("Возраст не может быть пустым."),
    ERROR_MESSAGE_EMPTY_ADDRESS("Адрес не может быть пустым."),
    ERROR_MESSAGE_WRONG_LENGTH_ADDRESS("Адрес не может быть длиннее 30 символов"),
    ERROR_MESSAGE_WRONG_SIZE_FIRST_LETTER_ADDRESS("Адрес должен начинаться с большой буквы"),
    ERROR_MESSAGE_EMPTY_PASSPORT("Номер паспорта не может быть пустым."),
    ERROR_AGE_NOT_INT("Ошибка: возраст должен быть числом."),
    ERROR_CREATE_GUEST("Ошибка при создании гостя: " ),
    ENTER_ID("Введите ID гостя: "),
    ERROR_MESSAGE_EMPTY_ID("Поле ID не может быть пустым"),
    UNKNOWN_ERROR("Неизвестная ошибка: %s"),
    EXISTING_GUEST("Гость с такими паспортными данными уже существует"),
    WRONG_GUEST_ID("Гостя с таким ID не существует"),
    GUEST_NOT_FOUND("Гостя с ID %d не существует."),
    CHECK_IN_SUCCESS("Гость c ID %d успешно заселен в отель %d."),
    GUEST_WITH_PASSPORT_EXIST("Гость с паспортными данными %s уже существует. "),
    EXIST_CHECKIIN("Гость уже заселен в другой отель."),
    ACCESS_CHECKIN("Гость может быть заселен.");


    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
