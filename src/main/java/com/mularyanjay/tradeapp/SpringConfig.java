package com.mularyanjay.tradeapp;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;
import com.mongodb.client.MongoDatabase;

@Configuration
public class SpringConfig {
    @Bean
    public DB getDb() throws UnknownHostException, MongoException {
        MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
        DB db = mongoURI.connectDB();
        db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
        System.out.println("DATABASE EEN SHEET CREATED");
        return db;
//    	MongoClient mc = new MongoClient(new MongoClientURI(System.getenv("MONGOHQ_URL")));
//    	MongoDatabase db = mc.getDatabase("mula");
//    	System.out.println("DATABASE EEN SHEET CREATED");
//    	return db;
    }
}
