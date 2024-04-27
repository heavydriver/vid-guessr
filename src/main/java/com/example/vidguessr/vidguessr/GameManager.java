package com.example.vidguessr.vidguessr;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class GameManager {
    public static final int MAX_LOCATIONS = 3;
    public static final String LATITUDE_KEY = "locationLatitude";
    public static final String LONGITUDE_KEY = "locationLongitude";
    public static final String URL_KEY = "videoURL";

    public static final List<String> highMotivationalExpressions = List.of("Outstanding", "Incredible", "Spectacular", "Remarkable", "Amazing");
    public static final List<String> mediumMotivationalExpressions = List.of("Great job", "Well done", "Keep it up", "Nice work", "Impressive effort");
    public static final List<String> lowMotivationalExpressions = List.of("Good try", "Keep practicing", "Don't give up", "You'll get there", "Better luck next time");

    private final String difficulty;
    private JSONArray allLocations;
    private List<Location> randomlySelectedLocations;

    public GameManager(String difficulty) {
        this.difficulty = difficulty;
        randomlySelectedLocations = new ArrayList<>();
        loadLocations();
    }

    public List<Location> getLocations() {
        pickRandomLocations();

        return randomlySelectedLocations;
    }

    private void loadLocations() {
        try {
            Object obj = new JSONParser().parse(new FileReader(
                    "src/main/resources/com/example/vidguessr/vidguessr/data.json"));
            JSONObject jsonObject = (JSONObject)obj;

            allLocations = (JSONArray)jsonObject.get(difficulty);
            System.out.println(allLocations);
        } catch (ParseException e) {
            System.out.println("Unable to parse JSON file");
        } catch (IOException e) {
            System.out.println("JSON file not found");
        }
    }

    private void pickRandomLocations() {
        List<Integer> randomIndexes = getRandomIndexes();

        for (int idx : randomIndexes) {
            JSONObject location = (JSONObject) allLocations.get(idx);
            randomlySelectedLocations.add(new Location(
                    Double.parseDouble((String) location.get(LATITUDE_KEY)),
                    Double.parseDouble((String) location.get(LONGITUDE_KEY)),
                    (String) location.get(URL_KEY)));
        }

        System.out.println(randomlySelectedLocations);
    }

    private  List<Integer> getRandomIndexes() {
        List<Integer> randomIndexes = new ArrayList<>();
        Random randomGenerator = new Random();

        while (randomIndexes.size() < MAX_LOCATIONS) {
            int randomNumber = randomGenerator.nextInt(allLocations.size());

            if (!randomIndexes.contains(randomNumber)) {
                randomIndexes.add(randomNumber);
            }
        }

        return randomIndexes;
    }

//    public static void main(String[] args) {
//        GameManager game = new GameManager("easy");
//    }
}
