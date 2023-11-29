package com.farad.client;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StartPageController {
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Заполните все поля!");
            alert.show();
        } else {
            try {
                //Создание объекта для работы с сервером
                Connection conn = Connection.getInstance("localhost", 8080);
                //Отправка данных
                String userInput;
                userInput = "auth " + loginField.getText().trim() + " " + passwordField.getText().trim();
                conn.sendRequest(userInput);

                //Получение результата
                String answer = conn.getRequest();

                if (answer.equals("success")) {
                    if (conn.getRequest().equals("1")) {
                        Stage newStage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-page.fxml"));

                        Parent root = loader.load();
                        Scene scene = new Scene(root);

                        newStage.setScene(scene);
                        newStage.setTitle("Admin Panel: " + loginField.getText().trim());

                        primaryStage.close();
                        newStage.show();
                    }
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Не удалось подключиться к серверу");
                alert.show();
            }
        }
    }
}
