package com.farad.db;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Connection {
    private final Logger l = Logger.getLogger(getClass().getName());
    private static Connection instance;

    private static final String address = "localhost";
    private static final int port = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream outObj;
    public static ObjectInputStream inObj;

    private Connection(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outObj = new ObjectOutputStream(socket.getOutputStream());
        inObj = new ObjectInputStream(socket.getInputStream());
    }

    public static Connection getInstance() throws IOException {
        if (instance == null) {
            instance = new Connection(address, port);
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

    public List<?> getObjects() throws IOException, ClassNotFoundException {
        return (List<?>) inObj.readObject();
    }

    public void sendObjects(List<?> sendingList) {
        try {
            outObj.writeObject(sendingList);
        } catch (IOException e) {
            l.log(Level.WARNING, "Ошибка в отправке данных");
        }
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