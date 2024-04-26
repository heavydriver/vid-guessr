package com.example.vidguessr.vidguessr;

import animatefx.animation.FadeIn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("VidGuessr");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/vidGuessrLogo.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        new FadeIn(scene.getRoot()).play();
    }

    public static void main(String[] args) {
        launch();
    }
}