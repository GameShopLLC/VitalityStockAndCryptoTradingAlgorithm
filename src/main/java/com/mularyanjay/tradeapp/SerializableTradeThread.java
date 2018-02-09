//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class SerializableTradeThread {

	private BigDecimal usd;
	private BigDecimal ltc;
	//buyprocessstate
	private String buyProcessState; //STANDBY, DESIRED_BUY, BOUGHT, DESIRED_SELL, SOLD
	private String lifeTimeState; //IDLE, TRADING, BUY_STUCK, SELL_STUCK, RESERVE;
	//private long desiredBuyTimeout; //Depends on tradegroup, change to BUY_STUCK
	//private long desiredSellToStuckTimeout; //Depends on tradegroup, change to SELL_STUCK
	//Need to implement a timer (Without thread.sleep???? -_- meh)
	private BigDecimal requestBuyPrice;
	private BigDecimal requestSellPrice;
	private BigDecimal requestedTotal;
	private BigDecimal requestedLtc;
	private BigDecimal currentPrice;
	private BigDecimal profit;
	private BigDecimal profitPercentage;
	private BigDecimal lastUsd;
	private String name;
	
	public SerializableTradeThread() {
		
	}

	public BigDecimal getUsd() {
		return usd;
	}

	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}

	public BigDecimal getLtc() {
		return ltc;
	}

	public void setLtc(BigDecimal ltc) {
		this.ltc = ltc;
	}

	public String getBuyProcessState() {
		return buyProcessState;
	}

	public void setBuyProcessState(String buyProcessState) {
		this.buyProcessState = buyProcessState;
	}

	public String getLifeTimeState() {
		return lifeTimeState;
	}

	public void setLifeTimeState(String lifeTimeState) {
		this.lifeTimeState = lifeTimeState;
	}

	public BigDecimal getRequestBuyPrice() {
		return requestBuyPrice;
	}

	public void setRequestBuyPrice(BigDecimal requestBuyPrice) {
		this.requestBuyPrice = requestBuyPrice;
	}

	public BigDecimal getRequestSellPrice() {
		return requestSellPrice;
	}

	public void setRequestSellPrice(BigDecimal requestSellPrice) {
		this.requestSellPrice = requestSellPrice;
	}

	public BigDecimal getRequestedTotal() {
		return requestedTotal;
	}

	public void setRequestedTotal(BigDecimal requestedTotal) {
		this.requestedTotal = requestedTotal;
	}

	public BigDecimal getRequestedLtc() {
		return requestedLtc;
	}

	public void setRequestedLtc(BigDecimal requestedLtc) {
		this.requestedLtc = requestedLtc;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getProfitPercentage() {
		return profitPercentage;
	}

	public void setProfitPercentage(BigDecimal profitPercentage) {
		this.profitPercentage = profitPercentage;
	}

	public BigDecimal getLastUsd() {
		return lastUsd;
	}

	public void setLastUsd(BigDecimal lastUsd) {
		this.lastUsd = lastUsd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
