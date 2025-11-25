package com.example;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

public class MongoConnection {

    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() {

        if (mongoClient == null) {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(
                                            Collections.singletonList(new ServerAddress(
                                                    System.getenv("MONGO_HOST"),   // Ex: localhost
                                                    Integer.parseInt(System.getenv("MONGO_PORT"))  // Ex: 27017
                                            ))
                                    )
                            )
                            .build()
            );
        }

        return mongoClient.getDatabase(System.getenv("MONGO_DB")); // Ex: testdb
    }
}

