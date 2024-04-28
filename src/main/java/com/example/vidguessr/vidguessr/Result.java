package com.example.vidguessr.vidguessr;

import animatefx.animation.FadeIn;
import animatefx.animation.SlideInLeft;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class Result {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private int totalScore;
    private List<Integer> roundScores;

    @FXML
    public Label finalScoreText;
    public Button playAgainButton;

    public void setScores(int finalScore, List<Integer> scores) {
        totalScore = finalScore;
        roundScores = scores;
    }

    public void displayResult() {
        finalScoreText.setText(String.valueOf(totalScore));
    }

    @FXML
    public void playAgain(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            root = fxmlLoader.load();

            scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);

            new SlideInLeft(root).play();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
