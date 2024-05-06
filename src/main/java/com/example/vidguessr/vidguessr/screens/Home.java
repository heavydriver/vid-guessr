/**
 * The Home screen of the game
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.screens;

import animatefx.animation.*;
import com.example.vidguessr.vidguessr.Main;
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

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Home implements Initializable
{
    public static String username = "";

    private Scene scene;
    private Parent root;

    private File usernameFile;

    @FXML
    public Button easyButton;
    @FXML
    public Button mediumButton;
    @FXML
    public Button hardButton;
    @FXML
    public Button submitButton;
    @FXML
    public TextField usernameField;
    @FXML
    public GridPane gameContainer;
    @FXML
    public GridPane usernameContainer;

    /**
     * Changes the scene to the Game scene when the user clicks an of the "Play" buttons
     * @param event the button click event
     */
    @FXML
    public void handleButtonClick(ActionEvent event)
    {
        String difficulty = "";

        if (event.getSource().equals(easyButton))
            difficulty = Game.EASY_DIFFICULTY;

        if (event.getSource().equals(mediumButton))
            difficulty = Game.MEDIUM_DIFFICULTY;

        if (event.getSource().equals(hardButton))
            difficulty = Game.HARD_DIFFICULTY;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game.fxml"));
            root = fxmlLoader.load();

            Game myGame = fxmlLoader.getController();
            myGame.setDifficulty(difficulty);

            scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);

            new SlideInRight(root).play();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Changes the state of the submit button based the text typed
     * @param event the KeyEvent
     */
    @FXML
    public void handleUsernameInput(KeyEvent event)
    {
        username = usernameField.getText();
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (username.length() >= 2 && username.length() <= 15)
        {
            submitButton.setDisable(false);
        } else
        {
            submitButton.setDisable(true);
        }
    }

    /**
     * Write the given username in a text file on submit button click or enter key press
     * @param event the button click event
     */
    @FXML
    public void submitUsername(ActionEvent event)
    {
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        if (username.length() >= 2 && username.length() <= 15)
        {
            try {
                PrintWriter usernameWriter = new PrintWriter(usernameFile);

                usernameWriter.print(username);

                usernameWriter.close();

                new FadeOutDown(usernameContainer).play();
                usernameContainer.setDisable(true);

                gameContainer.setDisable(false);
            } catch (FileNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * The method is called when the scene is initialized. Checks if a username file exists,
     * if yes then get the username, else prompt the user for a username.
     * @param url FXML file url
     * @param resourceBundle FXML resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        if (username.length() < 2)
        {
            try
            {
                usernameFile = new File("username.txt");

                if (usernameFile.exists())
                {
                    Scanner sc = new Scanner(usernameFile);

                    if (sc.hasNextLine())
                    {
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
                } else
                {
                    gameContainer.setDisable(true);

                    usernameContainer.setVisible(true);
                    usernameContainer.setDisable(false);

                    new FadeInUp(usernameContainer).play();

                    Platform.runLater(() -> submitButton.getScene().getRoot().requestFocus());
                }
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
}