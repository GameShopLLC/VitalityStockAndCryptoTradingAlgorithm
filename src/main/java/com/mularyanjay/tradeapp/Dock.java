package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class Dock {

	private String name;
	private BigDecimal usd;
	private BigDecimal ltc;
	
	public Dock() {
		
	}
	
	public Dock(String name) {
	
		this.name = name;
		this.usd = new BigDecimal("0");
		this.ltc = new BigDecimal("0");
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
	
	public BigDecimal getLtc(){
		return ltc;
	}
	
	public void setLtc(BigDecimal ltc){
		this.ltc = ltc;
	}

	public void addLtc(BigDecimal ltc) {
		this.ltc = this.ltc.add(ltc);
	}
	
	public void subtractLtc(BigDecimal ltc) {
		this.ltc = this.ltc.subtract(ltc);
	}
	
}
