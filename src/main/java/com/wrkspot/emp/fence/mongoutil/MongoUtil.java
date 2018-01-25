package com.wrkspot.emp.fence.mongoutil;

import com.mongodb.MongoClient;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.Serializable;

public class MongoUtil implements Serializable{
    private static Datastore datastore;
    private MongoUtil() {
        MongoClient client = new MongoClient(FenceGlossaries.getProperties().getProperty("mongo.host"), Integer.parseInt(FenceGlossaries.getProperties().getProperty("mongo.port")));
        datastore = new Morphia().createDatastore(client, FenceGlossaries.getProperties().getProperty("mongo.db.name"));
    }

    private static class SingletonHelper {
        private static final MongoUtil INSTANCE = new MongoUtil();
    }

    public static MongoUtil getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public static Datastore getMongoDatastore() {
        return datastore;
    }
}
