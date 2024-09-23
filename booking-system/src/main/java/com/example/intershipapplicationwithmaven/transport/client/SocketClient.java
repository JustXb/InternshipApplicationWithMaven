package com.example.intershipapplicationwithmaven.transport.client;

import com.example.intershipapplicationwithmaven.transport.dto.request.RequestDto;
import com.example.intershipapplicationwithmaven.transport.dto.response.ResponseDto;

import java.io.IOException;

public interface SocketClient <R extends RequestDto, V extends ResponseDto>{

    V sendRequest(R request);

    void connect(String host, int port) throws IOException;


    void sendData(String data) throws IOException;


    String receiveData() throws IOException;


    void disconnect() throws IOException;


    boolean isConnected();



}
