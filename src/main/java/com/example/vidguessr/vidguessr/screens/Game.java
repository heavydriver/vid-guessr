/**
 * The Game screen of the game
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.screens;

import animatefx.animation.*;
import com.example.vidguessr.vidguessr.Main;
import com.example.vidguessr.vidguessr.utility.GameManager;
import com.example.vidguessr.vidguessr.utility.Location;
import com.example.vidguessr.vidguessr.map.MyMap;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Game
{
    public static final String EASY_DIFFICULTY = "easy";
    public static final String MEDIUM_DIFFICULTY = "medium";
    public static final String HARD_DIFFICULTY = "hard";
    public static final double KM_TO_MILES = 0.621371d;
    public static final String GREEN_COLOR = "-fx-background-color: #41B06E;";
    public static final String ORANGE_COLOR = "-fx-background-color: #FF8A08;";
    public static final String RED_COLOR = "-fx-background-color: #F05454;";
    public static final String GREEN_SOUND = "sounds/green.mp3";
    public static final String ORANGE_SOUND = "sounds/orange.mp3";
    public static final String RED_SOUND = "sounds/red.mp3";

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
    @FXML
    public VBox mapContainer;
    @FXML
    public Button guessButton;
    @FXML
    public Button confirmButton;
    @FXML
    public AnchorPane mapView;
    @FXML
    public Button muteButton;
    @FXML
    public Button unmuteButton;
    @FXML
    public HBox roundResult;
    @FXML
    public Button closeMapButton;
    @FXML
    public Label roundScoreLabel;
    @FXML
    public Label roundOffByLabel;
    @FXML
    public Label roundTextLabel;
    @FXML
    public Button nextRoundButton;
    @FXML
    public ProgressIndicator loader;
    @FXML
    public VBox restartContainer;
    @FXML
    public Button restartButton;
    @FXML
    public Button clickHereMapLabel;

    /**
     * Sets the difficulty for this game and initializes the state of the game
     * @param difficulty the difficulty of the game
     */
    public void setDifficulty(String difficulty)
    {
        this.difficulty = difficulty;
        myGame = new GameManager(difficulty);
        locations = myGame.getLocations();
        currentRound = 1;
        roundScores = new ArrayList<>();
        mute = true;
        startRound();
    }

    /**
     * Starts the game round
     * If it is the final round, the user is sent to the result screen
     */
    public void startRound()
    {
        confirmButton.setDisable(true);
        guessButton.setDisable(true);
        muteButton.setDisable(true);
        unmuteButton.setDisable(true);

        roundResult.setVisible(false);
        roundResult.setDisable(true);

        closeMapButton.setVisible(true);
        closeMapButton.setDisable(false);

        roundResult.setStyle("-fx-background-color: #393E46;");

        if (currentRound <= locations.size())
        {
            Location currentLocation = locations.get(currentRound - 1);
            playVideo(currentLocation.getVideoURL());
            actualPosition = new GeoPosition(currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        } else
        {
            mediaView.getMediaPlayer().stop();

            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("result.fxml"));
                root = fxmlLoader.load();

                Result result = fxmlLoader.getController();
                result.setDifficulty(difficulty);
                result.setScores(totalScore, roundScores);

                scene = ((Node)confirmButtonEvent.getSource()).getScene();
                scene.setRoot(root);

                new SlideInRight(root).play();
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Starts playing the give video
     * @param videoURL the url of the video to play
     */
    public void playVideo(String videoURL)
    {
        loader.setVisible(true);
        loader.setDisable(false);

        Media media = new Media(Objects.requireNonNull(
                Main.class.getResource(videoURL)).toString());
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setAutoPlay(true);
        player.setMute(mute);
        player.setCycleCount(Integer.MAX_VALUE);

        // called when the video player is ready
        player.setOnReady(() ->
        {
            guessButton.setDisable(false);

            if (mute)
                muteButton.setDisable(false);
            else
                unmuteButton.setDisable(false);

            loader.setVisible(false);
            loader.setDisable(true);
        });

        // called when the video player has an error
        player.setOnError(() ->
        {
            loader.setVisible(false);
            loader.setDisable(true);

            restartContainer.setVisible(true);
            restartContainer.setDisable(false);

            restartContainer.toFront();
        });

        player.setOnStalled(() ->
        {
            loader.setVisible(false);
            loader.setDisable(true);

            restartContainer.setVisible(true);
            restartContainer.setDisable(false);

            restartContainer.toFront();
        });

        mediaView.setMediaPlayer(player);
    }

    /**
     * Shows the map modal when the user clicks the "Guess" button
     * @param event the button click button
     */
    @FXML
    public void showMap(ActionEvent event)
    {
//        mediaView.getMediaPlayer().pause();

        new Pulse(guessButton).play();

        mapContainer.setVisible(true);
        mapContainer.setDisable(false);
        guessButton.setDisable(true);

        new FadeInUp(mapContainer).play();

        if (mute)
            muteButton.setDisable(true);
        else
            unmuteButton.setDisable(true);

        if (!isGuessClicked)
        {
            SwingNode swingNode = new SwingNode();
            createSwingContent(swingNode);

            mapView.getChildren().add(swingNode);
            mapView.toBack();
        }
    }

    /**
     * Called when the user presses the "Confirm" button. The round score is shown. A map shows
     * how far the user's guess was from the actual location.
     * @param event the button click event
     */
    @FXML
    public void confirmPlayerLocation(ActionEvent event)
    {
        mediaView.getMediaPlayer().pause();

        confirmButton.setVisible(false);
        confirmButton.setDisable(true);

        roundResult.setVisible(true);
        roundResult.setDisable(false);

        closeMapButton.setVisible(false);
        closeMapButton.setDisable(true);

        GeoPosition playerPosition = mapController.getMarkerPos();

        int distance = (int) Math.round(mapController.calculateDistance(
                playerPosition, actualPosition));
        int score = Math.max((5000 - distance), 0);

        roundScores.add(currentRound - 1, score);
        totalScore += score;

        confirmButtonEvent = event;

        mapController.drawRoute(playerPosition, actualPosition);
        new FadeInUp(mapView).play();

        if (score >= 4000)
        {
            roundResult.setStyle(GREEN_COLOR);
            roundTextLabel.setText(GameManager.highMotivationalExpressions.get(
                    new Random().nextInt(GameManager.highMotivationalExpressions.size())) + "!");

            Media media = new Media(Objects.requireNonNull(
                    Main.class.getResource(GREEN_SOUND)).toString());
            MediaPlayer player = new MediaPlayer(media);
            player.play();

            player.setOnEndOfMedia(() -> player.dispose());
        } else if (score >= 1000)
        {
            roundResult.setStyle(ORANGE_COLOR);
            roundTextLabel.setText(GameManager.mediumMotivationalExpressions.get(
                    new Random().nextInt(GameManager.mediumMotivationalExpressions.size())) + "!");

            Media media = new Media(Objects.requireNonNull(
                    Main.class.getResource(ORANGE_SOUND)).toString());
            MediaPlayer player = new MediaPlayer(media);
            player.play();

            player.setOnEndOfMedia(() -> player.dispose());
        } else
        {
            roundResult.setStyle(RED_COLOR);
            roundTextLabel.setText(GameManager.lowMotivationalExpressions.get(
                    new Random().nextInt(GameManager.lowMotivationalExpressions.size())) + "!");

            Media media = new Media(Objects.requireNonNull(
                    Main.class.getResource(RED_SOUND)).toString());
            MediaPlayer player = new MediaPlayer(media);
            player.play();

            player.setOnEndOfMedia(() -> player.dispose());
        }

        roundScoreLabel.setText(String.valueOf(score));
        roundOffByLabel.setText("Off by " + (
                Math.round(distance * KM_TO_MILES * 100) / 100d) + " miles");

        new FadeInUp(roundResult).play();
    }

    /**
     * Starts the next round. Resets the state of the game.
     */
    public void startNextRound()
    {
        mediaView.getMediaPlayer().dispose();
        new FadeOutDown(mapContainer).play();
        mapContainer.setDisable(true);

        confirmButton.setVisible(true);
        confirmButton.setDisable(false);

        currentRound += 1;
        isGuessClicked = false;
        startRound();
    }

    /**
     * Closes the map modal. Called when user presses the "X" button or the "esc" key
     * @param event the button click or key press event
     */
    @FXML
    public void closeMap(ActionEvent event)
    {
        new FadeOutDown(mapContainer).play();
        mapContainer.setDisable(true);
        guessButton.setDisable(false);

        if (mute)
            muteButton.setDisable(false);
        else
            unmuteButton.setDisable(false);

//        mediaView.getMediaPlayer().play();
    }

    /**
     * Toggles the mute/unmute state of the video player
     * @param event the button click event
     */
    @FXML
    public void muteUnmute(ActionEvent event)
    {
        mute = !mute;
        mediaView.getMediaPlayer().setMute(mute);

        if (mute)
        {
            muteButton.setVisible(true);
            muteButton.setDisable(false);

            new Pulse(muteButton).play();

            unmuteButton.setVisible(false);
            unmuteButton.setDisable(true);
        } else
        {
            muteButton.setVisible(false);
            muteButton.setDisable(true);

            new Pulse(unmuteButton).play();

            unmuteButton.setVisible(true);
            unmuteButton.setDisable(false);
        }
    }

    /**
     * Called when the user presses the "Restart" button after a video fails to load.
     * Sends the user the Home screen where they can start the game again.
     * @param event the button click event
     */
    @FXML
    public void restartGame(ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            root = fxmlLoader.load();

            scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);

            new SlideInLeft(root).play();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Hides the "Click here" label
     * @param event the button click event
     */
    @FXML
    public void hideMapLabel(ActionEvent event) {
        clickHereMapLabel.setVisible(false);
        clickHereMapLabel.setDisable(true);
    }

    /**
     * The map component uses Java Swing. SwingNode allows
     * a Java Swing component to be used with JavaFX.
     * @param swingNode the node to create the map component in
     */
    private void createSwingContent(final SwingNode swingNode) {
        isGuessClicked = true;

        SwingUtilities.invokeLater(() ->
        {
            mapController = new MyMap(confirmButton);
            map = mapController.getMap();

            map.setPreferredSize(
                    new Dimension((int) mapView.getWidth(), (int) mapView.getHeight()));

            swingNode.setContent(map);
            swingNode.setCache(false);
        });
    }
}
