package com.grandvista.backend.database;

import com.grandvista.backend.config.ConfigLoader;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoDBConnection() {
        String uri = ConfigLoader.getProperty("spring.data.mongodb.uri");
        mongoClient = MongoClients.create(uri);
        // Extract database name from URI or use default
        String dbName = extractDatabaseName(uri);
        database = mongoClient.getDatabase(dbName);
        System.out.println("Connected to MongoDB database: " + dbName);
    }

    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    private String extractDatabaseName(String uri) {
        // Extract database name from MongoDB URI
        // Format: mongodb+srv://user:pass@host/dbname?params
        try {
            int lastSlash = uri.lastIndexOf('/');
            int questionMark = uri.indexOf('?', lastSlash);
            if (questionMark > 0) {
                return uri.substring(lastSlash + 1, questionMark);
            } else {
                return uri.substring(lastSlash + 1);
            }
        } catch (Exception e) {
            return "grand_vista_db"; // default
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
