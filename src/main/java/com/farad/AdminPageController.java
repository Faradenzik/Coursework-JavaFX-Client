package com.farad;

import com.farad.tables.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class AdminPageController {
    @FXML
    private AnchorPane adminMain;

    @FXML
    void showFirst(ActionEvent event) {
        Connection conn;
        try {
            conn = Connection.getInstance("localhost", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        conn.sendRequest("get");
        DataPacket<User> dataPacket = (DataPacket<User>) conn.getObjects();
        List<User> userList = dataPacket.getData();

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
