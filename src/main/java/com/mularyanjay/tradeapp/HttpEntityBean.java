//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpEntityBean {
	
	HashingAlgorithm hashingAlgorithm;
	
	private HttpHeaders headers = new HttpHeaders();
	private HttpEntity entity;
	public HttpEntityBean(){
		hashingAlgorithm = new HashingAlgorithm();
//		headers.add("accept", "application/json");
//        headers.add("content-type", "application/json");
//        headers.add("CB-ACCESS-KEY", hashingAlgorithm.getKey());
//        headers.add("CB-ACCESS-SIGN", hashingAlgorithm.getHash(defaultUrl));
//        headers.add("CB-ACCESS-TIMESTAMP", hashingAlgorithm.getTimestamp());
//        headers.add("CB-ACCESS-PASSPHRASE", hashingAlgorithm.getPassphrase());
//        entity = new HttpEntity(headers);

	}
	
	public HttpEntity <String> getEntityFromUrl(String url){
		headers.clear();
		headers.add("accept", "application/json");
        headers.add("content-type", "application/json");
        headers.add("CB-ACCESS-KEY", hashingAlgorithm.key);
        headers.add("CB-ACCESS-SIGN", hashingAlgorithm.getHash(url));
        headers.add("CB-ACCESS-TIMESTAMP", hashingAlgorithm.timestamp);
        headers.add("CB-ACCESS-PASSPHRASE", hashingAlgorithm.passphrase);
        //headers.set
		//return new HttpEntity<String>("", headers);
		return new HttpEntity<>("", headers);
	}
	
	public HttpEntity <String> postEntityFromUrl(String url, String jsonBody){
		headers.clear();
		headers.add("accept", "application/json");
        headers.add("content-type", "application/json");
        headers.add("CB-ACCESS-KEY", hashingAlgorithm.key);
        headers.add("CB-ACCESS-SIGN", hashingAlgorithm.postHash(url, jsonBody));
        headers.add("CB-ACCESS-TIMESTAMP", hashingAlgorithm.timestamp);
        headers.add("CB-ACCESS-PASSPHRASE", hashingAlgorithm.passphrase);
        //headers.set
		//return new HttpEntity<String>("", headers);
		return new HttpEntity<>(jsonBody, headers);
	}
	
	public HttpEntity<String> getLocalEntityFromUrl(String url, String contentType) {
		headers.clear();
		if (contentType != null && contentType != "") {
			headers.add("contentType", contentType);
		} else {
			headers.add("contentType", "text");
		}
		return new HttpEntity<>("",headers);
	}
	
	//@SuppressWarnings("unchecked") w00t
	public HttpEntity<String> postLocalEntityFromUrl (String url, String contentType, String dataType, String data) {
		headers.clear();
		if (contentType != null && contentType != "") {
			headers.add("contentType", contentType);
		} else {
			headers.add("contentType", "text");
		}
		if (dataType != null && dataType != "") {
			headers.add("dataType", dataType);
		} else {
			headers.add("dataType", "text");
			
		}
		return new HttpEntity<String>(data, headers);
		
	}
	
	public HttpEntity<TickerData> postLocalEntityFromUrl (String url, String contentType, String dataType, TickerData data) {
		headers.clear();
		if (contentType != null && contentType != "") {
			headers.add("contentType", contentType);
		} else {
			headers.add("contentType", "text");
		}
		if (dataType != null && dataType != "") {
			headers.add("dataType", dataType);
		} else {
			headers.add("dataType", "text");
			
		}
		return new HttpEntity<TickerData>(data, headers);
		
	}
	
	//the fuck?
//	public HttpEntity postEntityFromUrl(String url, String body){
//		//headers.remove(    )
//		return null;
//	}
}
