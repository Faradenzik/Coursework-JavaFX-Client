package com.farad.controllers;

import java.io.IOException;

import com.farad.db.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static com.farad.StartPage.getAlert;

public class StartPageController {
    public static String username;
    private Stage primaryStage;
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void enterButtonOnPressed(ActionEvent event) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            getAlert("warning", "Заполните все поля!");
        } else {
            try {
                //Создание объекта для работы с сервером
                Connection conn = Connection.getInstance();
                //Отправка данных
                String userInput;
                userInput = "auth " + loginField.getText().trim() + " " + passwordField.getText().trim();
                conn.sendRequest(userInput);

                //Получение результата
                String answer = conn.getRequest();

                if (answer.equals("success")) {
                    String role = conn.getRequest();
                    username = loginField.getText().trim();

                    Stage newStage = new Stage();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-page.fxml"));

                    Parent root = loader.load();
                    Scene scene = new Scene(root, 1500, 800);

                    newStage.setScene(scene);
                    newStage.setTitle(role + " panel: " + username);
                    AdminPageController adminController = loader.getController();;
                    adminController.initialize(role);

                    primaryStage.close();
                    newStage.show();
                } else {
                    getAlert("error", "Проверьте правильность введенных данных");
                }
            } catch (IOException e) {
                getAlert("error", "Не удалось подключиться к серверу");
            }
        }
    }
}
