package com.farad.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class Connection {
    private static Connection instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Connection(String address, int port) {
        try {
            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInstance(String address, int port) {
        if (instance == null) {
            instance = new Connection( address, port);
        }
        return instance;
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String getRequest() {
        String request = null;
        try {
            request = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
