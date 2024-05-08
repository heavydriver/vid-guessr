/**
 * Randomly selects five locations for the game
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.example.vidguessr.vidguessr.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class GameManager
{
    public static final int MAX_LOCATIONS = 5;
    public static final String LATITUDE_KEY = "locationLatitude";
    public static final String LONGITUDE_KEY = "locationLongitude";
    public static final String URL_KEY = "videoURL";

    public static final List<String> highMotivationalExpressions = List.of(
            "Outstanding", "Incredible", "Spectacular", "Remarkable", "Amazing");
    public static final List<String> mediumMotivationalExpressions = List.of(
            "Great job", "Well done", "Keep it up", "Nice work", "Impressive effort");
    public static final List<String> lowMotivationalExpressions = List.of(
            "Good try", "Keep practicing", "Don't give up", "You'll get there",
            "Better luck next time");

    private final String difficulty;
    private JSONArray allLocations;
    private List<Location> randomlySelectedLocations;

    /**
     * Constructs a new GameManager object
     * @param difficulty the difficulty of the game
     */
    public GameManager(String difficulty)
    {
        this.difficulty = difficulty;
        randomlySelectedLocations = new ArrayList<>();
        loadLocations();
    }

    /**
     * Accessor method to get the 5 randomly generated locations
     * @return a List of 5 random Locations
     */
    public List<Location> getLocations()
    {
        pickRandomLocations();

        return randomlySelectedLocations;
    }

    /**
     * Loads all the locations for the give difficulty from data.json
     */
    private void loadLocations()
    {
        try
        {
            Object obj = new JSONParser().parse(new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(Main.class.getResourceAsStream("data.json")))));
            JSONObject jsonObject = (JSONObject)obj;

            allLocations = (JSONArray)jsonObject.get(difficulty);
        } catch (ParseException e) {
            System.out.println("Unable to parse JSON file");
        } catch (IOException e) {
            System.out.println("JSON file not found");
        }
    }

    /**
     * Creates a List of 5 Locations from the random indexes
     */
    private void pickRandomLocations()
    {
        List<Integer> randomIndexes = getRandomIndexes();

        for (int idx : randomIndexes)
        {
            JSONObject location = (JSONObject) allLocations.get(idx);
            randomlySelectedLocations.add(new Location(
                    Double.parseDouble((String) location.get(LATITUDE_KEY)),
                    Double.parseDouble((String) location.get(LONGITUDE_KEY)),
                    (String) location.get(URL_KEY)));
        }
    }

    /**
     * Generates a list of 5 random numbers from 0 to the size of allLocations List
     * @return the 5 randomly selected indexes
     */
    private  List<Integer> getRandomIndexes()
    {
        List<Integer> randomIndexes = new ArrayList<>();
        Random randomGenerator = new Random();

        while (randomIndexes.size() < MAX_LOCATIONS)
        {
            int randomNumber = randomGenerator.nextInt(allLocations.size());

            if (!randomIndexes.contains(randomNumber))
                randomIndexes.add(randomNumber);
        }

        return randomIndexes;
    }
}
