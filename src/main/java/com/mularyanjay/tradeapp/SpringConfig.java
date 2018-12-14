package com.mularyanjay.tradeapp;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;
import com.mongodb.client.MongoDatabase;

@Configuration
public class SpringConfig {
    @Bean
    public MongoDatabase getDb() throws UnknownHostException, MongoException {
//        MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
//        DB db = mongoURI.connectDB();
//        db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
//        System.out.println("DATABASE EEN SHEET CREATED");
    	MongoClientURI mcURI = new MongoClientURI(System.getenv("MONGOHQ_URL"));
    	
//    	String user; // the user name
//    	 String database; // the name of the database in which the user is defined
//    	 char[] password; // the password as a character array
    	 // ...

//    	 MongoCredential credential = MongoCredential.createCredential(mcURI.getUsername(), mcURI.getDatabase(), mcURI.getPassword());

//    	 MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();
//        return db;
    	MongoClient mc = new MongoClient(mcURI);
    	MongoDatabase db = mc.getDatabase("heroku_f2jqwmg9");
    	System.out.println("DATABASE EEN SHEET CREATED");
    	return db;
    }
}
