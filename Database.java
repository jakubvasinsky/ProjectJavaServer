package sample;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {



    public static void main(String[] args) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        try (MongoClient mongoClient =  MongoClients.create("mongodb://localhost:27017")) {


            List<String> databases = mongoClient.listDatabaseNames().into(new ArrayList<>());
            System.out.println("database" + databases);


            System.out.println(" ");
            MongoDatabase database = mongoClient.getDatabase("javaServer");

            System.out.println("database name: " + database.getName());

            for (String name: database.listCollectionNames()){
                System.out.println(name);
            }

            System.out.println(" ");


            MongoCollection<Document> collection=database.getCollection("users");


            Document doc= new Document("name", "Jakub Vašinský")
                    .append("age", 20 )
                    ;


            collection.insertOne(doc);



            System.out.println(" ");

            System.out.println("Print all");
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                System.out.println(document.toJson());
            }

        }
    }
}