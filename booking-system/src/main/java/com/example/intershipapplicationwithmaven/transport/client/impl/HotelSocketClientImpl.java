package com.example.intershipapplicationwithmaven.transport.client.impl;

import com.example.intershipapplicationwithmaven.transport.client.SocketClient;
import com.example.intershipapplicationwithmaven.transport.dto.request.RequestDto;
import com.example.intershipapplicationwithmaven.transport.dto.response.ResponseDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HotelSocketClientImpl implements SocketClient<RequestDto, ResponseDto> {
    private static final String host = "localhost";
    private static final int port = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    @Override
    public ResponseDto sendRequest(RequestDto request) {

            return null;

    }

    @Override
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void sendData(String data) throws IOException {
        if (isConnected()) {
            out.println(data);
        } else {
            throw new IOException("Not connected to the server.");
        }
    }

    @Override
    public String receiveData() throws IOException {
        if (isConnected()) {
            return in.readLine();
        } else {
            throw new IOException("Not connected to the server.");
        }
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            in.close();
            out.close();
            socket.close();
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
}
