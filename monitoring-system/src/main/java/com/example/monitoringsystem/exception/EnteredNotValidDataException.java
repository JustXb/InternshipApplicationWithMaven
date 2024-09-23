package com.example.monitoringsystem.exception;

public class EnteredNotValidDataException extends Exception{

    public EnteredNotValidDataException(String inputDataIsNull) {
        super(inputDataIsNull);
    }
}
