package com.example.vidguessr.vidguessr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Home {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Button easyButton;
    public Button mediumButton;
    public Button hardButton;

    @FXML
    protected void handleButtonClick(ActionEvent event) throws IOException {
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

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }
}