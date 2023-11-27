package com.farad.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StartPageController {
    @FXML
    private Label authResultText;

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
            String serverAddress = "localhost"; // Адрес сервера
            int portNumber = 8080; // Порт сервера

            try (
                    Socket socket = new Socket(serverAddress, portNumber);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                //Отправка данных
                String userInput;
                userInput = "auth " + loginField.getText().trim() + " " + passwordField.getText().trim();
                out.println(userInput);

                //Получение результата
                String answer = in.readLine();

                if (answer.equals("success")) {
                    if (in.readLine().equals("1")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-page.fxml"));
                        Scene scene = new Scene(loader.load(), 1500, 800);
                        Stage newStage = new Stage();
                        newStage.setTitle("Admin Panel: " + loginField.getText().trim());
                        newStage.setResizable(false);
                        newStage.setScene(scene);
                        newStage.show();

                        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
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
