package com.example.vidguessr.vidguessr;

import animatefx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Home implements Initializable {
    public static String username = "";

    private Stage stage;
    private Scene scene;
    private Parent root;

    private File usernameFile;

    @FXML
    public Button easyButton;
    public Button mediumButton;
    public Button hardButton;
    public Button submitButton;
    public TextField usernameField;
    public GridPane gameContainer;
    public GridPane usernameContainer;

    @FXML
    public void handleButtonClick(ActionEvent event) throws IOException {
        String difficulty = "";

        if (event.getSource().equals(easyButton))
            difficulty = Game.EASY_DIFFICULTY;

        if (event.getSource().equals(mediumButton))
            difficulty = Game.MEDIUM_DIFFICULTY;

        if (event.getSource().equals(hardButton))
            difficulty = Game.HARD_DIFFICULTY;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game.fxml"));
        root = fxmlLoader.load();

        Game myGame = fxmlLoader.getController();
        myGame.setDifficulty(difficulty);

        scene = ((Node)event.getSource()).getScene();
        scene.setRoot(root);

        new SlideInRight(root).play();
    }

    public void handleUsernameInput(KeyEvent event) {
        username = usernameField.getText();
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (username.length() >= 2 && username.length() <= 15) {
            submitButton.setDisable(false);
        } else {
            submitButton.setDisable(true);
        }
    }

    public void submitUsername(ActionEvent event) {
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (username.length() >= 2 && username.length() <= 15) {
            System.out.println(username);

            try {
                PrintWriter usernameWriter = new PrintWriter(usernameFile);

                usernameWriter.print(username);

                usernameWriter.close();

                new FadeOutDown(usernameContainer).play();
                usernameContainer.setDisable(true);

                gameContainer.setDisable(false);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(username);

        if (username.length() < 2) {
            try {
                usernameFile = new File(Objects.requireNonNull(getClass().getResource("username.txt")).toURI());
                Scanner sc = new Scanner(usernameFile);

                if (sc.hasNextLine()) {
                    username = sc.nextLine();
                    System.out.println(username);
                } else {
                    gameContainer.setDisable(true);

                    usernameContainer.setVisible(true);
                    usernameContainer.setDisable(false);

                    new FadeInUp(usernameContainer).play();

                    Platform.runLater(() -> submitButton.getScene().getRoot().requestFocus());
                }

                sc.close();
            } catch (IOException | URISyntaxException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}