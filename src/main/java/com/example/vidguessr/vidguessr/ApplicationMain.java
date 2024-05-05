/**
 * Creates the JavaFX application
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr;

import animatefx.animation.FadeIn;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ApplicationMain extends Application {

    /**
     * Starts the JavaFX application
     * @param stage where the scene is displayed
     */
    @Override
    public void start(Stage stage)
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ApplicationMain.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setTitle("VidGuessr");
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    ApplicationMain.class.getResourceAsStream("images/vidGuessrLogo.png"))));
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            new FadeIn(scene.getRoot()).play();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Launches the JavaFX application
     * @param args the arguments
     */
    public static void main(String[] args)
    {
        launch();
    }
}