package com.example.vidguessr.vidguessr;

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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class LeaderBoardDatabase {
    private static final String connectionString = "mongodb+srv://savage:521lbUrQzLPo0joH@cluster0.vzwtxpe.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    private String gameDifficulty;
    private MongoClientSettings settings;

    public LeaderBoardDatabase(String difficulty) {
        gameDifficulty = difficulty;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
    }

    public void updateLeaderboard(String username, int score) {
        try(MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("vidGuessr");
            MongoCollection<Document> collection = database.getCollection(gameDifficulty);

            Document quey = new Document("username", username);

            Bson updates = Updates.combine(Updates.max("score", score));

            UpdateOptions options = new UpdateOptions().upsert(true);

            try {
                UpdateResult result = collection.updateOne(quey, updates, options);

                System.out.println("Modified document count: " + result.getModifiedCount());
                System.out.println("Upserted id: " + result.getUpsertedId());
            } catch (MongoException e) {
                System.out.println("Unable to update database" + e);
            }
        }
    }

    public Map<String, Integer> getLeaderboard() {
        Map<String, Integer> leaderboard = new LinkedHashMap<>();

        try(MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("vidGuessr");
            MongoCollection<Document> collection = database.getCollection(gameDifficulty);

            MongoCursor<Document> cursor = collection.find().sort(Sorts.descending("score")).limit(5).iterator();

            try {
                while (cursor.hasNext()) {
                    Document data = cursor.next();

                    String username = data.getString("username");
                    int score = data.getInteger("score");

                    leaderboard.put(username, score);
                }
            } finally {
                cursor.close();
            }
        }

        return leaderboard;
    }

//    public static void main(String[] args) {
//        // Create a new client and connect to the server
//        try (MongoClient mongoClient = MongoClients.create(settings)) {
//            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
//            MongoCollection<Document> collection = database.getCollection("movies");
//
//            Document doc = collection.find(eq("title", "Back to the Future")).first();
//            if (doc != null) {
//                System.out.println(doc.toJson());
//            } else {
//                System.out.println("No matching documents found.");
//            }
//        }
//    }
}
