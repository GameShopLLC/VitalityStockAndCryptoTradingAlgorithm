package com.mularyanjay.tradeapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPropertiesValues {

	//@Value("${server.ssl.key-store}")
	private String keyStore;
	
	public ApplicationPropertiesValues() {
		
	}
	
	public String getKeyStore() {
		return keyStore;
	}
}
