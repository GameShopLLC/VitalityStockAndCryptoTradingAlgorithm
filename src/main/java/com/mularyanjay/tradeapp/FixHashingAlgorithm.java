package com.mularyanjay.tradeapp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

public class FixHashingAlgorithm {

	public String key;
	public String password;
	public String secret;
	public String timestamp = "";
	
	public FixHashingAlgorithm() {
		password = new String("mulamulamula");
		key = new String("9343abebdceb6edec76b417c75b33b1b");
		secret = new String("W9oFNci42NPp00NEN78D4nAWgOTyPhU/RN2/Bi+jNYsgsZDygmnkRzaVefnbR29TYUVKP524GtnOEMzxBORnMw==");
		
	}
	
	public String getHash() {
		timestamp = new String(Instant.now().getEpochSecond() + "");
		Mac sha256 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		timestamp = new String(sdf.format(new Date(timestamp)));
		try {
//			String prehash = timestamp + "GET".toUpperCase() + requestPath; 
			//+ "";
			String prehash = "52=" + timestamp + "|35=A" + "|34=1" + "|49=" + key + "|56=Coinbase" + "|554=" + password;
			System.out.println(prehash);
			byte[] secretDecoded = Base64.getDecoder().decode(secret);
			SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, "HmacSHA256");
			sha256 = (Mac) Mac.getInstance("HmacSHA256").clone();
			sha256.init(keyspec);
			System.out.println(Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes())));
			
			return Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
		} catch(CloneNotSupportedException | InvalidKeyException | NoSuchAlgorithmException e){
			e.printStackTrace();
			throw new RuntimeErrorException(new Error("Cannot set up authentication headers"));
			
		}
//		return "";
	}
}
