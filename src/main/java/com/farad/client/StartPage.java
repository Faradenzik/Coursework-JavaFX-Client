package com.farad.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartPage extends Application {
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
}