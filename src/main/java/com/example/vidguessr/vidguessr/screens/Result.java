/**
 * The Result screen of the game
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.screens;

import animatefx.animation.FadeOut;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInUp;
import com.example.vidguessr.vidguessr.Main;
import com.example.vidguessr.vidguessr.utility.LeaderBoardDatabase;
import com.mongodb.MongoException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Result implements Initializable
{
    private Scene scene;
    private Parent root;

    private String difficulty;
    private int totalScore;
    private List<Integer> roundScores;
    private Map<String, Integer> leaderboard;
    private List<Label> playerLabels;
    private List<Label> scoreLabels;

    @FXML
    public Label finalScoreText;
    @FXML
    public Button playAgainButton;
    @FXML
    public Label player1;
    @FXML
    public Label player2;
    @FXML
    public Label player3;
    @FXML
    public Label player4;
    @FXML
    public Label player5;
    @FXML
    public Label score1;
    @FXML
    public Label score2;
    @FXML
    public Label score3;
    @FXML
    public Label score4;
    @FXML
    public Label score5;
    @FXML
    public Label dbErrorText1;
    @FXML
    public Label dbErrorText2;
    @FXML
    public ProgressIndicator loader;
    @FXML
    public GridPane leaderboardTable;
    @FXML
    public Label difficultyLabel;

    /**
     * Sets the difficulty for this game and initializes the state of the game
     * @param difficulty the difficulty of the game
     */
    public void setDifficulty(String difficulty)
    {
        this.difficulty = difficulty;
    }

    /**
     * Sets the given scores to the instance variables
     * @param finalScore the total score of all the rounds
     * @param scores a List of scores from each round
     */
    public void setScores(int finalScore, List<Integer> scores)
    {
        totalScore = finalScore;
        roundScores = scores;
    }

    /**
     * Displays the totalScore on the results page and loads the leaderboard
     * @throws MongoException if there is an error in loading the database
     */
    public void displayResult() throws MongoException
    {
        LeaderBoardDatabase db = new LeaderBoardDatabase(difficulty);
        db.updateLeaderboard(Home.username, totalScore);

        leaderboard = db.getLeaderboard();
    }

    /**
     * Displays the leaderboard with the information form the database
     */
    public void displayLeaderBoard()
    {
        int idx = 0;
        scene = playAgainButton.getScene();

        for (Map.Entry<String, Integer> player : leaderboard.entrySet())
        {
            playerLabels.get(idx).setText(player.getKey());
            scoreLabels.get(idx).setText(String.valueOf(player.getValue()));

            idx += 1;
        }
    }

    /**
     * Called when the user presses the "Play Again" button.
     * The user is sent to the Home screen where they can start the game again.
     * @param event the button click event
     */
    @FXML
    public void playAgain(ActionEvent event)
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
     * The method is called when the scene is initialized. Creates a Task to update the
     * database and get the latest leaderboard data from the database.
     * @param url FXML file url
     * @param resourceBundle FXML resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        playAgainButton.sceneProperty().addListener(((observableValue, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                try
                {
                    // creates a new task to call the displayResult method
                    Task<Void> task = new Task<Void>()
                    {
                        @Override
                        protected Void call() throws Exception
                        {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    finalScoreText.setText(String.valueOf(totalScore));
                                    difficultyLabel.setText("(" + difficulty + ")");
                                }
                            });

                            displayResult();
                            return null;
                        }
                    };

                    // display the loader when the Task is running
                    task.setOnRunning((event) ->
                    {
                        loader.setVisible(true);
                        loader.setDisable(false);

                        playAgainButton.setDisable(true);
                    });

                    // disable the loader and update some variables when the Task is successful
                    task.setOnSucceeded((event) ->
                    {
                        new FadeOut(loader).play();
                        loader.setDisable(true);

                        playAgainButton.setDisable(false);

                        playerLabels = Arrays.asList(player1, player2, player3, player4, player5);
                        scoreLabels = Arrays.asList(score1, score2, score3, score4, score5);

                        displayLeaderBoard();

                        leaderboardTable.setVisible(true);
                        new SlideInUp(leaderboardTable).play();
                    });

                    // show an error message if the Task fails to run
                    task.setOnFailed((event) ->
                    {
                        new FadeOut(loader).play();
                        loader.setDisable(true);

                        playAgainButton.setDisable(false);

                        dbErrorText1.setVisible(true);
                        dbErrorText2.setVisible(true);
                    });

                    // creates a new thread
                    ExecutorService executorService = Executors.newFixedThreadPool(1);

                    // execute the Task on the new thread
                    executorService.execute(task);

                    // close the thread after the Task is executed
                    executorService.shutdown();
                } catch (Exception e)
                {
                    System.out.println(e.getMessage());

                    dbErrorText1.setVisible(true);
                    dbErrorText2.setVisible(true);
                }
            }
        }));
    }
}
