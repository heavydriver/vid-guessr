package com.example.vidguessr.vidguessr;

import animatefx.animation.*;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    public static final String EASY_DIFFICULTY = "easy";
    public static final String MEDIUM_DIFFICULTY = "medium";
    public static final String HARD_DIFFICULTY = "hard";

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String difficulty;
    private GameManager myGame;
    private List<Location> locations;
    private int currentRound;
    private MyMap mapController;
    private JXMapViewer map;
    private GeoPosition actualPosition;
    private boolean isGuessClicked;
    private int totalScore;
    private List<Integer> roundScores;
    private ActionEvent confirmButtonEvent;
    private boolean mute;


    @FXML
    public MediaView mediaView;
    public VBox mapContainer;
    public Button guessButton;
    public Button confirmButton;
    public AnchorPane mapView;
    public Button muteButton;
    public Button unmuteButton;
    public HBox roundResult;
    public Button closeMapButton;
    public Label roundScoreLabel;
    public Label roundOffByLabel;
    public Label roundTextLabel;
    public Button nextRoundButton;

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        myGame = new GameManager(difficulty);
        locations = myGame.getLocations();
        currentRound = 1;
        roundScores = new ArrayList<>();
        mute = true;
        startRound();
    }

    public void startRound() {
        confirmButton.setDisable(true);
        guessButton.setDisable(true);
        muteButton.setDisable(true);
        unmuteButton.setDisable(true);

        roundResult.setVisible(false);
        roundResult.setDisable(true);

        closeMapButton.setVisible(true);
        closeMapButton.setDisable(false);

        roundResult.setStyle("-fx-background-color: #393E46;");

        if (currentRound <= locations.size()) {
            Location currentLocation = locations.get(currentRound - 1);
            playVideo(currentLocation.getVideoURL());
            actualPosition = new GeoPosition(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            System.out.println("done");
            System.out.println(totalScore);
            System.out.println(roundScores);
            mediaView.getMediaPlayer().stop();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("result.fxml"));
                root = fxmlLoader.load();

                Result result = fxmlLoader.getController();
                result.setScores(totalScore, roundScores);
                result.displayResult();

                scene = ((Node)confirmButtonEvent.getSource()).getScene();
                scene.setRoot(root);

                new SlideInRight(root).play();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void playVideo(String videoURL) {
        Media media = new Media(videoURL);
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setAutoPlay(true);
        player.setMute(mute);
        player.setCycleCount(Integer.MAX_VALUE);

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                guessButton.setDisable(false);

                if (mute)
                    muteButton.setDisable(false);
                else
                    unmuteButton.setDisable(false);
            }
        });

        mediaView.setMediaPlayer(player);
    }

    public void showMap(ActionEvent event) {
        mediaView.getMediaPlayer().pause();

        new Pulse(guessButton).play();

        mapContainer.setVisible(true);
        mapContainer.setDisable(false);
        guessButton.setDisable(true);

        new FadeInUp(mapContainer).play();

        if (mute)
            muteButton.setDisable(true);
        else
            unmuteButton.setDisable(true);

        if (!isGuessClicked) {
            SwingNode swingNode = new SwingNode();
            createSwingContent(swingNode);

            mapView.getChildren().add(swingNode);
            mapView.toBack();
        }
    }

    public void confirmPlayerLocation(ActionEvent event) {
        confirmButton.setVisible(false);
        confirmButton.setDisable(true);

        roundResult.setVisible(true);
        roundResult.setDisable(false);

        closeMapButton.setVisible(false);
        closeMapButton.setDisable(true);

        GeoPosition playerPosition = mapController.getMarkerPos();

        int distance = (int) Math.round(mapController.calculateDistance(playerPosition, actualPosition));
        int score = Math.max((5000 - distance), 0);

        roundScores.add(currentRound - 1, score);
        totalScore += score;

        confirmButtonEvent = event;

        mapController.drawRoute(playerPosition, actualPosition);
        new FadeInUp(mapView).play();

        if (score >= 4000) {
            roundResult.setStyle("-fx-background-color: #41B06E;");
            roundTextLabel.setText(GameManager.highMotivationalExpressions.get(new Random().nextInt(GameManager.highMotivationalExpressions.size())) + "!");
        } else if (score >= 1000) {
            roundResult.setStyle("-fx-background-color: #FF8A08;");
            roundTextLabel.setText(GameManager.mediumMotivationalExpressions.get(new Random().nextInt(GameManager.mediumMotivationalExpressions.size())) + "!");
        } else {
            roundResult.setStyle("-fx-background-color: #D24545;");
            roundTextLabel.setText(GameManager.lowMotivationalExpressions.get(new Random().nextInt(GameManager.lowMotivationalExpressions.size())) + "!");
        }

        roundScoreLabel.setText(String.valueOf(score));
        roundOffByLabel.setText("Off by " + (Math.round(distance * 0.621371d * 100) / 100d) + " miles");

        new FadeInUp(roundResult).play();
    }

    public void startNextRound() {
        new FadeOutDown(mapContainer).play();
//        mapContainer.setVisible(false);
        mapContainer.setDisable(true);

        confirmButton.setVisible(true);
        confirmButton.setDisable(false);

        currentRound += 1;
        isGuessClicked = false;
        startRound();
    }

    public void closeMap(ActionEvent event) {
        new FadeOutDown(mapContainer).play();
//        mapContainer.setVisible(false);
        mapContainer.setDisable(true);
        guessButton.setDisable(false);

        if (mute)
            muteButton.setDisable(false);
        else
            unmuteButton.setDisable(false);

        mediaView.getMediaPlayer().play();
    }

    public void muteUnmute(ActionEvent event) {
        mute = !mute;
        mediaView.getMediaPlayer().setMute(mute);

        if (mute) {
            muteButton.setVisible(true);
            muteButton.setDisable(false);

            new Pulse(muteButton).play();

            unmuteButton.setVisible(false);
            unmuteButton.setDisable(true);
        } else {
            muteButton.setVisible(false);
            muteButton.setDisable(true);

            new Pulse(unmuteButton).play();

            unmuteButton.setVisible(true);
            unmuteButton.setDisable(false);
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        isGuessClicked = true;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mapController = new MyMap(confirmButton);
                map = mapController.getMap();

                map.setPreferredSize(new Dimension((int) mapView.getWidth(), (int) mapView.getHeight()));

                swingNode.setContent(map);
            }
        });
    }
}
