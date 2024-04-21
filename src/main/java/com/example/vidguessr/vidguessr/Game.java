package com.example.vidguessr.vidguessr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.util.List;

public class Game {
    public static final String EASY_DIFFICULTY = "easy";
    public static final String MEDIUM_DIFFICULTY = "medium";
    public static final String HARD_DIFFICULTY = "hard";

    private String difficulty;
    private GameManager myGame;
    private List<Location> locations;
    private Location currentLocation;
    private int currentRound;

    @FXML
    public MediaView mediaView;
    public VBox mapContainer;
    public Button guessButton;
    public Button confirmButton;

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        myGame = new GameManager(difficulty);
        locations = myGame.getLocations();
        currentRound = 1;
        startRound();
    }

    public void startRound() {
        if (currentRound <= locations.size()) {
            currentLocation = locations.get(currentRound - 1);
            playVideo(currentLocation.getVideoURL());
        } else {
            System.out.println("done");
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

//        mapContainer.getChildren().addFirst(map);
        mapContainer.setVisible(true);
        mapContainer.setDisable(false);
//        currentRound += 1;
//        startRound();
    }
}
