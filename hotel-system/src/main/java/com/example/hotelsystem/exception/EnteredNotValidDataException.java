package com.example.hotelsystem.exception;

public class EnteredNotValidDataException extends Exception{

    public EnteredNotValidDataException(String inputDataIsNull) {
        super(inputDataIsNull);
    }
}
