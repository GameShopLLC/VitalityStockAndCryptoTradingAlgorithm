//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class Account {
	
	private String id;
    private String currency;
    private BigDecimal balance;
    private BigDecimal available;
    private BigDecimal hold;
    private String profile_id;
    
	public Account(){
		
		
	}

	public Account(String id, String currency, BigDecimal balance, BigDecimal available,BigDecimal hold,String profile_id) {

		this.id = id;
		this.currency = currency;
		this.balance = balance;
		this.available = available;
		this.hold = hold;
		this.profile_id = profile_id;

	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	public BigDecimal getHold() {
		return hold;
	}

	public void setHold(BigDecimal hold) {
		this.hold = hold;
	}

	public String getProfile_id() {
		return profile_id;
	}

	public void setProfile_id(String profile_id) {
		this.profile_id = profile_id;
	}
	
}
