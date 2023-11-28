package com.farad.client;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StartPageController {
    private Connection conn = StartPage.conn;
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void enterButtonOnPressed(ActionEvent event) {

        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Заполните все поля!");
            alert.show();
        } else {
            //Создание объекта для работы с сервером
            conn = Connection.getInstance("localhost", 8080);
            try {
                //Отправка данных
                String userInput;
                userInput = "auth " + loginField.getText().trim() + " " + passwordField.getText().trim();
                conn.sendRequest(userInput);

                //Получение результата
                String answer = conn.getRequest();

                if (answer.equals("success")) {
                    if (conn.getRequest().equals("1")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-page.fxml"));
                        Scene scene = new Scene(loader.load(), 1500, 800);
                        Stage newStage = new Stage();
                        newStage.setTitle("Admin Panel: " + loginField.getText().trim());
                        newStage.setResizable(false);
                        newStage.setScene(scene);
                        newStage.show();

                        ((Button) event.getSource()).getScene().getWindow().hide();
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
