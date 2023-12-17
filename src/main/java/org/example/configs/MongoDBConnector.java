package org.example.configs;

import org.bson.Document;

public interface MongoDBConnector {
    public void initializeMongoClient();
    public void connectToMongoDb();
    public void disconnect();
    public void insertData(Document document);
}
