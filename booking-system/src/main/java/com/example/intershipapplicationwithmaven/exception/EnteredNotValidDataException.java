package com.example.intershipapplicationwithmaven.exception;

public class EnteredNotValidDataException extends Exception{

    public EnteredNotValidDataException(String inputDataIsNull) {
        super(inputDataIsNull);
    }
}
