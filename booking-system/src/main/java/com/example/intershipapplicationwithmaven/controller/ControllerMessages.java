package com.example.intershipapplicationwithmaven.controller;

public enum ControllerMessages {
    GUEST_ADDED("Гость %s успешно добавлен "),
    GUEST_UPDATED("Гость с ID %d успешно обновлен."),
    GUEST_DELETED("Гость с ID %d успешно удален, доступность отеля %d увеличена."),
    GUEST_DELETED_WITHOUT_HOTEL("Гость с ID %d успешно удален. Гость не проживал в отеле."),

    GUEST_NOT_FOUND("Гостя с ID %d не существует."),

    GUEST_ID_NOT_NULL("ID гостя не может быть пустым"),
    HOTEL_ID_NOT_NULL("ID отеля не может быть пустым"),

    HOTEL_SERVICE_UNAVAILABLE("Сервис отелей недоступен."),
    CHECK_IN_SUCCESS("Гость c ID %d успешно заселен в отель %d."),
    CHECK_IN_ERROR("Ошибка заселения: %s"),
    GENERAL_ERROR("Ошибка: %s"),
    ADD_GUEST_ERROR("Ошибка добавления гостя: %s"),
    UPDATE_GUEST_ERROR("Ошибка при обновлении гостя с ID %d: %s"),
    DELETE_GUEST_ERROR("Ошибка при удалении гостя с ID %d: %s"),
    CHECK_IN_VALIDATION_ERROR("Ошибка заселения: %s"),
    CHECK_IN_AVAILABILITY_ERROR("Ошибка заселения: отель %d недоступен."),
    CHECK_IN_ERROR_WITH_ID("Ошибка при заселении гостя с ID %d: %s"),
    CHECK_IN_NO_VACANCY("Ошибка заселения: в отеле %d нет свободных мест.");

    private final String message;

    ControllerMessages(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
