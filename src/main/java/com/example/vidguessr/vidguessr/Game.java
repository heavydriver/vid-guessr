package com.example.vidguessr.vidguessr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Game {
    public static final String EASY_DIFFICULTY = "easy";
    public static final String MEDIUM_DIFFICULTY = "medium";
    public static final String HARD_DIFFICULTY = "hard";

    private String difficulty;

    @FXML
    public MediaView mediaView;

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        playVideo();
    }

    public void playVideo() {
        Media media = new Media("https://res.cloudinary.com/dacy5mp1g/video/upload/v1627368105/samples/sea-turtle.mp4");
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setAutoPlay(true);
        player.setMute(true);
        player.setCycleCount(Integer.MAX_VALUE);

        mediaView.setMediaPlayer(player);
    }

    public void showMap(ActionEvent event) {

    }
}
