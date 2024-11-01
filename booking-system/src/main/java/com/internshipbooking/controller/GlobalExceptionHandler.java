package com.internshipbooking.controller;

import com.example.EventType;
import com.internshipbooking.exception.ResponseException;
import com.internshipbooking.service.BookingService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final BookingService bookingService;

    public GlobalExceptionHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseException> handleJsonParseError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) cause);
        }

        bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.JSON_ERROR.getMessage(ex.getLocalizedMessage()));
        ResponseException responseException = new ResponseException(ControllerMessages.JSON_ERROR.getMessage(ex.getLocalizedMessage()));
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ResponseException> handleInvalidFormatException(InvalidFormatException ex) {
        String fieldName = ex.getPath().get(0).getFieldName();
        String invalidValue = ex.getValue().toString();

        bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.JSON_WRONG_TYPE_SERIALIZATION.getMessage(invalidValue, fieldName));

        ResponseException responseException = new ResponseException(ControllerMessages.JSON_ERROR_WRONG_TYPE.getMessage(invalidValue, fieldName));
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseException> handleResponseStatusException(ResponseStatusException ex) {
        bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.GENERAL_ERROR.getMessage(ex.getReason()));
        ResponseException responseException = new ResponseException(ex.getReason());
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseException> handleGeneralException(Exception ex) {
        bookingService.sendBookingToMonitoring(EventType.MISTAKE, ControllerMessages.GENERAL_ERROR.getMessage(ex.getMessage()));
        ResponseException responseException = new ResponseException(ex.getMessage());
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }
}

