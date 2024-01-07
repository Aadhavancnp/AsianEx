package com.tourism.asianex.Services;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.tourism.asianex.PropertiesCache;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.logging.Logger;

public class MongoService {
    private static final Logger LOGGER = Logger.getLogger(MongoService.class.getName());

    private static final ConnectionString connectionString = new ConnectionString(PropertiesCache.getInstance().getProperty("mongodb.uri"));
    private static volatile MongoClient mongoClient;

    private MongoService() {
    }

    private static MongoClient getMongoClient() {
        if (mongoClient == null) {
            synchronized (MongoService.class) {
                if (mongoClient == null) {
                    CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                            CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                            MongoClientSettings.getDefaultCodecRegistry()
                    );
                    MongoClientSettings settings = MongoClientSettings.builder()
                            .applyConnectionString(connectionString)
                            .retryWrites(true)
                            .codecRegistry(codecRegistry)
                            .build();
                    mongoClient = MongoClients.create(settings);
                }
            }
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        String databaseName = connectionString.getDatabase();
        if (databaseName == null) {
            throw new IllegalStateException("Database name cannot be null");
        }
        return getMongoClient().getDatabase(databaseName);
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

    public static void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
