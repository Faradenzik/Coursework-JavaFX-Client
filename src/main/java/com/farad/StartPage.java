package com.farad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class StartPage extends Application {
    private static Alert alert;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start-page.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Авторизация");
        primaryStage.setResizable(false);

        // Получаем контроллер главного окна
        StartPageController startPageController = loader.getController();
        startPageController.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Optional<ButtonType> getAlert(String type, String text) {
        switch (type) {
            case "warning": {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание");
                break;
            }
            case "error": {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                break;
            }
            case "confirmation": {
                ButtonType yes = new ButtonType("Да", ButtonBar.ButtonData.YES);
                ButtonType no = new ButtonType("Нет", ButtonBar.ButtonData.NO);
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().setAll(yes, no);
                alert.setTitle("Подтверждение");

                break;
            }
        }
        alert.setContentText(text);
        alert.setHeaderText(null);

        return alert.showAndWait();
    }
}