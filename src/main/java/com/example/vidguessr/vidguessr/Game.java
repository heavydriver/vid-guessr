package com.example.vidguessr.vidguessr;

import animatefx.animation.FadeIn;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    @FXML
    public MediaView mediaView;
    public VBox mapContainer;
    public Button guessButton;
    public Button confirmButton;
    public AnchorPane mapView;

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        myGame = new GameManager(difficulty);
        locations = myGame.getLocations();
        currentRound = 1;
        roundScores = new ArrayList<>();
        startRound();
    }

    public void startRound() {
        confirmButton.setDisable(true);

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

                new FadeIn(root).play();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void playVideo(String videoURL) {
        Media media = new Media(videoURL);
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setAutoPlay(true);
        player.setMute(true);
        player.setCycleCount(Integer.MAX_VALUE);

        mediaView.setMediaPlayer(player);
    }

    public void showMap(ActionEvent event) {
        mapContainer.setVisible(true);
        mapContainer.setDisable(false);
        guessButton.setDisable(true);

        if (!isGuessClicked) {
            SwingNode swingNode = new SwingNode();
            createSwingContent(swingNode);

            mapView.getChildren().add(swingNode);
            mapView.toBack();
        }
    }

    public void confirmPlayerLocation(ActionEvent event) {
        GeoPosition playerPosition = mapController.getMarkerPos();

        int distance = (int) Math.round(mapController.calculateDistance(playerPosition, actualPosition));
        int score = Math.max((5000 - distance), 0);

        roundScores.add(currentRound - 1, score);
        totalScore += score;

        mapContainer.setVisible(false);
        mapContainer.setDisable(true);
        guessButton.setDisable(false);

        confirmButtonEvent = event;

        startNextRound();
    }

    public void startNextRound() {
        currentRound += 1;
        isGuessClicked = false;
        startRound();
    }

    public void closeMap(ActionEvent event) {
        mapContainer.setVisible(false);
        mapContainer.setDisable(true);
        guessButton.setDisable(false);
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
