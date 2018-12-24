package com.mularyanjay.tradeapp;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator {
	private final PasswordAuthentication passwordAuthentication;
	  public ProxyAuthenticator(String user, String password) {
	    passwordAuthentication = new PasswordAuthentication(user, password.toCharArray());
	  }

	  @Override
	  protected PasswordAuthentication getPasswordAuthentication() {
	    return passwordAuthentication;
	  }
}
