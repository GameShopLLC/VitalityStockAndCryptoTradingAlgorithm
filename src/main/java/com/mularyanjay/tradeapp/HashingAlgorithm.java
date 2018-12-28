//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Component;

@Component
public class HashingAlgorithm {

//	passphrase 0b1v4yb271f8c
//
//	Key: bde7c18ff32b42bcff6d27afae438b3b
//	Secret??? (Runhash? Sameshit?): It48s80Wap0MFwtmUix1UpyfSpAQXNLOsYqscbFkGWYWcbpqzHMiLUtsHyuXzamyZba8+wjoPvtBEr7y91KqDg==

	//yg5p598a9hm
	//e5e184da7a9472728d25ebfe1b2b96e7
	//+bCndXi4YfWdtRGhubkppq8A3mwhEKx44JcHviHDmFv2EfPl73Ei9mHdf579PCtUggxs4A6akotaK+MIRPtpXg==
	
	
	public String passphrase;// = "youngmulababy";//"or2otyvvoj";//"q1foebvdgon";//hmfd9om60ei" //"hmfd9om60ei";//"yg5p598a9hm";//DISCARDED //"0b1v4yb271f8c";//2) "d42s35ynk16w"; //1)  "0b1v4yb271f8c";
	public String key;// = "7f7acace4bd4b4acb053929b7169bea3";//"1528df56017b4a0e606caddd6e08cf18"; //"716a23260f84427d4fcb6de5f74b5449";//"e5e184da7a9472728d25ebfe1b2b96e7"; //DISCARDED //"bde7c18ff32b42bcff6d27afae438b3b"; //2 )"9f0d81710ceb625214480ad99157c547";//1)  "bde7c18ff32b42bcff6d27afae438b3b";
	public String secret;// = "W7mzbaKv7zOUAcd/kC19MXO5ACE2DtyLHsBp4qmpBc/clIYRmi3LzzMO9JtA3CirGCYmhYThiQAUBVFHFba5nw==";//"LJLo2T072PIsxnmlg+ToPJXoLbULvSp7M0QIRLrraMpwEDvPuTWYNTBs+9N4u2ZJEnyLu12hozfnKRBm3jMthg=="; //"Z0iKEMnBjPZPwzwHm4xl97Dr/REj6rgIveBu+csZgfbcGmB+AM3wCY8LgOd8JF+J3pNGVTo6GCj0GgpeJ7Z34w==";//"+bCndXi4YfWdtRGhubkppq8A3mwhEKx44JcHviHDmFv2EfPl73Ei9mHdf579PCtUggxs4A6akotaK+MIRPtpXg=="; //DISCARDED //"It48s80Wap0MFwtmUix1UpyfSpAQXNLOsYqscbFkGWYWcbpqzHMiLUtsHyuXzamyZba8+wjoPvtBEr7y91KqDg==";
	 //2)    de"MgvN8QubN5qKVWmxCfIH9yJMAMDQxq+A/eYC58EGHKi4BX8igJdQOUYffs8gtgVturHAZm78CA/GUZG78ksL8g==";
			//1) "It48s80Wap0MFwtmUix1UpyfSpAQXNLOsYqscbFkGWYWcbpqzHMiLUtsHyuXzamyZba8+wjoPvtBEr7y91KqDg==";

	String timestamp = "";
	public HashingAlgorithm() {
//		
		passphrase = new String("mulamulamula");//youngmulababy");//"or2otyvvoj";//"q1foebvdgon";//hmfd9om60ei" //"hmfd9om60ei";//"yg5p598a9hm";//DISCARDED //"0b1v4yb271f8c";//2) "d42s35ynk16w"; //1)  "0b1v4yb271f8c";
		key = new String("b2f6737d9363d8f573fd0756f33c8c10");//"9343abebdceb6edec76b417c75b33b1b");//"6774f0a3439dbada8059f60a4823b509");//"bedd3d91ab030847d8267776d7e7197f");//"7f7acace4bd4b4acb053929b7169bea3");//"1528df56017b4a0e606caddd6e08cf18"; //"716a23260f84427d4fcb6de5f74b5449";//"e5e184da7a9472728d25ebfe1b2b96e7"; //DISCARDED //"bde7c18ff32b42bcff6d27afae438b3b"; //2 )"9f0d81710ceb625214480ad99157c547";//1)  "bde7c18ff32b42bcff6d27afae438b3b";
		secret = new String("nsGR71LbBoshFmeUiC1C5unqHx6/a1U0YMmWgReAW8ppjmGqtDtT9sJxRmKYZfdX8ZSxab7BeD3dpAAQQWfqXA==");//"W9oFNci42NPp00NEN78D4nAWgOTyPhU/RN2/Bi+jNYsgsZDygmnkRzaVefnbR29TYUVKP524GtnOEMzxBORnMw==");//"hJct0xTqGw4JBlyhdeNeRSspxzVhq+Cw6B3mJRgp93GhjrAhkX9HpxtXExh4IGbOYNW5SVxpKNeBxW8kWuYMPQ==");//"dagzop52vEmSs5fRiCIpLwxccGNJH49XdxvAbck3o2zLh6ERp024gVx/QQNSk3soIoPyHB4wLVcVg9D3z6UAnQ==");//"W7mzbaKv7zOUAcd/kC19MXO5ACE2DtyLHsBp4qmpBc/clIYRmi3LzzMO9JtA3CirGCYmhYThiQAUBVFHFba5nw==");//"LJLo2T072PIsxnmlg+ToPJXoLbULvSp7M0QIRLrraMpwEDvPuTWYNTBs+9N4u2ZJEnyLu12hozfnKRBm3jMthg=="; //"Z0iKEMnBjPZPwzwHm4xl97Dr/REj6rgIveBu+csZgfbcGmB+AM3wCY8LgOd8JF+J3pNGVTo6GCj0GgpeJ7Z34w==";//"+bCndXi4YfWdtRGhubkppq8A3mwhEKx44JcHviHDmFv2EfPl73Ei9mHdf579PCtUggxs4A6akotaK+MIRPtpXg=="; //DISCARDED //"It48s80Wap0MFwtmUix1UpyfSpAQXNLOsYqscbFkGWYWcbpqzHMiLUtsHyuXzamyZba8+wjoPvtBEr7y91KqDg==";
		 	}
	
	public String getHash(String requestPath){
		timestamp = new String(Instant.now().getEpochSecond() + "");
		Mac sha256 = null;
		try {
			String prehash = timestamp + "GET".toUpperCase() + requestPath; 
			//+ "";
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
		
	}
	
	public String postHash(String requestPath, String body){
		timestamp = Instant.now().getEpochSecond() + "";
		Mac sha256 = null;
		try {
			String prehash = timestamp + "POST".toUpperCase() + "/orders" + body; 
			//+ "";
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
	}

//	public String getPassphrase() {
//		return passphrase;
//	}
//
//	public void setPassphrase(String passphrase) {
//		this.passphrase = passphrase;
//	}
//
//	public String getKey() {
//		return key;
//	}
//
//	public void setKey(String key) {
//		this.key = key;
//	}
//
//	public String getSecret() {
//		return secret;
//	}
//
//	public void setSecret(String secret) {
//		this.secret = secret;
//	}
//
//	public String getTimestamp() {
//		return timestamp;
//	}
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
	
	
}
