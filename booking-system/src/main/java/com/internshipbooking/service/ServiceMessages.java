package com.internshipbooking.service;

public enum ServiceMessages {
    WRONG_GUEST_ID("Гостя с таким ID не существует"),
    GUEST_NOT_FOUND("Гостя с ID %d не существует."),
    EXISTING_GUEST("Гость с такими паспортными данными уже существует"),
    GUEST_WITH_PASSPORT_EXIST("Гость с паспортными данными %s уже существует. "),
    EXIST_CHECK_IN("Гость уже заселен в другой отель."),
    ACCESS_CHECKIN("Гость может быть заселен.");


    private final String message;

    ServiceMessages(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
