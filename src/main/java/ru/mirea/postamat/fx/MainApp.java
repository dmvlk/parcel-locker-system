package ru.mirea.postamat.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ru/mirea/postamat/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
                MainApp.class.getResource("/ru/mirea/postamat/css/app.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Постаматы - Вход");
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(Parent root, String title) {
        primaryStage.getScene().setRoot(root);
        primaryStage.setTitle(title);
    }

    public static Stage getStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}