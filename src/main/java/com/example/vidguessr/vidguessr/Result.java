package com.example.vidguessr.vidguessr;

import animatefx.animation.FadeIn;
import animatefx.animation.SlideInLeft;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Result implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private int totalScore;
    private List<Integer> roundScores;
    private Map<String, Integer> leaderboard;
    private List<Label> playerLabels;
    private List<Label> scoreLabels;

    @FXML
    public Label finalScoreText;
    public Button playAgainButton;
    public Label player1;
    public Label player2;
    public Label player3;
    public Label player4;
    public Label player5;
    public Label score1;
    public Label score2;
    public Label score3;
    public Label score4;
    public Label score5;

    public void setScores(int finalScore, List<Integer> scores) {
        totalScore = finalScore;
        roundScores = scores;
    }

    public void displayResult() {
        finalScoreText.setText(String.valueOf(totalScore));

        LeaderBoardDatabase db = new LeaderBoardDatabase("easy");
        db.updateLeaderboard("savage", totalScore);

        leaderboard = db.getLeaderboard();
    }

    public void displayLeaderBoard() {
        int idx = 0;
        scene = playAgainButton.getScene();

        for (Map.Entry<String, Integer> player : leaderboard.entrySet()) {
            playerLabels.get(idx).setText(player.getKey());
            scoreLabels.get(idx).setText(String.valueOf(player.getValue()));

            idx += 1;
        }
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playAgainButton.sceneProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (newScene != null) {
                displayResult();

                playerLabels = Arrays.asList(player1, player2, player3, player4, player5);
                scoreLabels = Arrays.asList(score1, score2, score3, score4, score5);

                displayLeaderBoard();
            }
        }));
    }
}
