package com.farad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.logging.Logger;


public class AdminPageController {
    private final Logger l = Logger.getLogger(getClass().getName());

    @FXML
    private AnchorPane adminMain;

    @FXML
    void showUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("showUsers.fxml"));
        Parent root = loader.load();

        adminMain.getChildren().setAll(root);
    }

    @FXML
    void showCustomers() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("showCustomers.fxml"));
        Parent root = loader.load();

        adminMain.getChildren().setAll(root);
    }

    @FXML
    void showCars() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("showCars.fxml"));
        Parent root = loader.load();

        adminMain.getChildren().setAll(root);
    }
}
