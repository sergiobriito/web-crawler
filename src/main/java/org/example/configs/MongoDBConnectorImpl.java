package org.example.configs;

import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBConnectorImpl implements MongoDBConnector {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBConnectorImpl() {
        connectToMongoDb();
    }

    static {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
    }

    public void initializeMongoClient() {
        try {
            String connectionString = String.format("mongodb://%s:%s@%s:%d/admin",
                    Constants.MONGO_DB_USER,
                    Constants.MONGO_DB_PASSWORD,
                    Constants.MONGO_DB_HOST,
                    Constants.MONGO_DB_PORT);

            ConnectionString connString = new ConnectionString(connectionString);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .build();

            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            this.collection = database.getCollection(Constants.MONGO_DB_COL_NAME);

            System.out.println("Connected to the database successfully");
        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }

    public void connectToMongoDb() {
        initializeMongoClient();
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Disconnected from the database");
        }
    }

    public void insertData(Document document) {
        try {
            if (collection != null) {
                collection.insertOne(document);
            } else {
                System.err.println("Collection is null. Unable to insert data.");
            }
        } catch (Exception e) {
            System.err.println("Error inserting data into MongoDB: " + e.getMessage());
        }
    }
}
