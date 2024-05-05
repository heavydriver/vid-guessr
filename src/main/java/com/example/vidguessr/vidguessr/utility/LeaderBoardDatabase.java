/**
 * Connects to the remote database to maintain the leaderboard
 * @author Varun Mange
 * Collaborators: Azfar Islam
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.utility;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderBoardDatabase
{
    public static final String DATABASE_KEY = "vidGuessr";
    public static final String USERNAME_KEY = "username";
    public static final String SCORE_KEY = "score";

    private static final String connectionString =
            "mongodb+srv://savage:521lbUrQzLPo0joH@cluster0.vzwtxpe.mongodb.net/" +
                    "?retryWrites=true&w=majority&appName=Cluster0";

    private final String gameDifficulty;
    private final MongoClientSettings settings;

    /**
     * Constructs a new LeaderBoardDatabase object
     * @param difficulty the difficulty of the current game
     */
    public LeaderBoardDatabase(String difficulty)
    {
        gameDifficulty = difficulty;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
    }

    /**
     * Updates the score of the current player in the database if their current score is greater
     * than their previous score. If the user does not exist in the database,
     * a new entry is created.
     * @param username the username to match in the database
     * @param score the final score of the current game out of 25,000
     * @throws MongoException if there is an error while updating the database
     */
    public void updateLeaderboard(String username, int score) throws MongoException
    {
        try(MongoClient mongoClient = MongoClients.create(settings))
        {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_KEY);
            MongoCollection<Document> collection = database.getCollection(gameDifficulty);

            Document quey = new Document(USERNAME_KEY, username);

            Bson updates = Updates.combine(Updates.max(SCORE_KEY, score));

            UpdateOptions options = new UpdateOptions().upsert(true);

            UpdateResult result = collection.updateOne(quey, updates, options);
        }
    }

    /**
     * Access the leaderboard information for the given difficulty in the database
     * @return a Map of at most 5 users on the leaderboard sorted in
     * descending order based on their score
     */
    public Map<String, Integer> getLeaderboard()
    {
        Map<String, Integer> leaderboard = new LinkedHashMap<>();

        try(MongoClient mongoClient = MongoClients.create(settings))
        {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_KEY);
            MongoCollection<Document> collection = database.getCollection(gameDifficulty);

            MongoCursor<Document> cursor = collection.find().sort(
                    Sorts.descending(SCORE_KEY)).limit(5).iterator();

            try
            {
                while (cursor.hasNext())
                {
                    Document data = cursor.next();

                    String username = data.getString(USERNAME_KEY);
                    int score = data.getInteger(SCORE_KEY);

                    leaderboard.put(username, score);
                }
            } finally
            {
                cursor.close();
            }
        }

        return leaderboard;
    }
}
