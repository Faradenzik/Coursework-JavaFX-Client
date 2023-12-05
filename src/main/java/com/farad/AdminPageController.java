package com.farad;

import com.farad.tables.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AdminPageController {
    private final Logger l = Logger.getLogger(getClass().getName());

    @FXML
    private AnchorPane adminMain;

    @FXML
    void showFirst() throws IOException, ClassNotFoundException {
        Connection conn;
        try {
            conn = Connection.getInstance("localhost", 8080);
        } catch (IOException e) {
            l.log(Level.WARNING, "Ошибка подключения к серверу.");
            return;
        }

        conn.sendRequest("get users");
        List<?> received = conn.getObjects();
        List<User> userList = (List<User>) received;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("showUsers.fxml"));
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ShowUsersController usersController = loader.getController();
        usersController.setTable(userList);

        adminMain.getChildren().setAll(node);
    }

    @FXML
    void showSecond(ActionEvent event) {

    }
}
