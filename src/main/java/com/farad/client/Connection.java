package com.farad.client;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Connection {
    private final Logger l = Logger.getLogger(Connection.class.getName());
    private static Connection instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream outObj;
    private ObjectInputStream inObj;

    private Connection(String address, int port) throws IOException {

            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outObj = new ObjectOutputStream(socket.getOutputStream());
            inObj = new ObjectInputStream(socket.getInputStream());

    }

    public static Connection getInstance(String address, int port) throws IOException {
        if (instance == null) {
            instance = new Connection( address, port);
        }
        return instance;
    }

    public void sendRequest(String request) {
        try {
            out.println(request);
        } catch (Exception e) {
            l.log(Level.WARNING, "Ошибка в отправке данных");
        }
    }

    public String getRequest() {
        String request = null;
        try {
            request = in.readLine();
        } catch (Exception e) {
            l.log(Level.WARNING, "Ошибка в получении данных");
        }
        return request;
    }

    public List<?> getObjects() {
        List<?> lst = null;
        try {
            lst = (List<?>) inObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lst;
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
