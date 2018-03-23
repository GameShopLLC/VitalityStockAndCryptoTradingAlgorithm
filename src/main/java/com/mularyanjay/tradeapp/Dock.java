package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class Dock {

	private String name;
	private BigDecimal usd;
	
	public Dock() {
		
	}
	
	public Dock(String name) {
	
		this.name = name;
		this.usd = new BigDecimal("0");
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getUsd() {
		return usd;
	}

	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}
	
	public void addUsd(BigDecimal usd) {
		this.usd = this.usd.add(usd);
	}
	
	public void subtractUsd(BigDecimal usd) {
		this.usd = this.usd.subtract(usd);
	}
	
	
}
