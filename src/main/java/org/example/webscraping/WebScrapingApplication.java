package org.example.webscraping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

@SpringBootApplication
public class WebScrapingApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebScrapingApplication.class, args);



    }
}
