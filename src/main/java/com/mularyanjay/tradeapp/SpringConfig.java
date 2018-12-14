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
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@Configuration
public class SpringConfig {
//    @Bean
//    public MongoDatabase getDb() throws UnknownHostException, MongoException {
////        MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
////        DB db = mongoURI.connectDB();
////        db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
////        System.out.println("DATABASE EEN SHEET CREATED");
////    	MongoClientURI mcURI = new MongoClientURI("mongodb://heroku_f2jqwmg9:rhbo24kbsoms80tu922onqgn9m@ds053459.mlab.com:53459/heroku_f2jqwmg9");//System.getenv("MONGOHQ_URL"));
//    	MongoClientURI mcURI = new MongoClientURI("mongodb://heroku:e83tQNgfvUPf-CqsElCaJIty2lKdi9r9nVIF77cOqyD-OMf5Rd6hZSumNpqFDDJYo_hxyud020k_9ArokV3ZRg@candidate.53.mongolayer.com:11386,candidate.10.mongolayer.com:11310/app85221945");//
////    	String user; // the user name
////    	 String database; // the name of the database in which the user is defined
////    	 char[] password; // the password as a character array
//    	 // ...
//
//    	 MongoCredential credential = MongoCredential.createScramSha1Credential(mcURI.getUsername(), mcURI.getDatabase(), mcURI.getPassword());
//
//    	 MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();
////        return db;
//    	MongoClient mc = new MongoClient(new ServerAddress("localhost", 27017),
//                Arrays.asList(credential), options);
//    	
//    	MongoDatabase db = mc.getDatabase(mcURI.getDatabase());
//    	
////    	System.out.println(db.);
//    	System.out.println("DATABASE EEN SHEET CREATED");
//    	return db;
//    }
}
