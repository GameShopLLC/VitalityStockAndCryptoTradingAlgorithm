//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class TradeThread {

	private BigDecimal usd;
	private BigDecimal ltc;
	//buyprocessstate
	private String buyProcessState; //STANDBY, DESIRED_BUY, BOUGHT, DESIRED_SELL, SOLD
	private String lifeTimeState; //IDLE, TRADING, STUCK, RESERVE;
	private float desiredBuyTimeout; //Depends on tradegroup
	private float desiredSellToStuckTimeout; //Depends on tradegroup
	//Need to implement a timer (Without thread.sleep???? -_- meh)
	private BigDecimal requestBuyPrice;
	private BigDecimal requestSellPrice;
	private BigDecimal requestedTotal;
	private BigDecimal requestedLtc;
	private BigDecimal currentPrice;
	private BigDecimal profit;
	private BigDecimal profitPercentage;
	private BigDecimal lastUsd;
	
	//private BigDecimal currentPrice;
	
	public TradeThread() {
		
	}
	
	public TradeThread(BigDecimal initialUSD, float whatDBT, float whatDSTST) {
	
		setUsd(initialUSD);
		setLastUsd(initialUSD);
		setLtc(new BigDecimal("0"));
		setBuyProcessState("STANDBY");
		setLifeTimeState("IDLE");
		setDesiredBuyTimeout(whatDBT);
		setDesiredSellToStuckTimeout(whatDSTST);
		setRequestedLtc(new BigDecimal("0"));
		setCurrentPrice(new BigDecimal("0"));
	}
	
	//set desired buy and what not.  Timer/timeouts for if it gets stuck
	//Remember to set timer/timeouts
	public void deploy(Carrot carrot) {
		
		if (getCurrentPrice().compareTo(carrot.getLow().subtract(new BigDecimal(".01"))) == 1) {
		//Have to make sure things are set up correctly
			
		//At this point I would place buy order with api	
		setRequestBuyPrice(carrot.getLow().subtract(new BigDecimal(".01")));
		//placeBuyOrder();
		//Need to calculate totals and then do transaction
		//A buy order will deduct dollars and want ltc,
		//but will possess no ltc until it is met.
		setRequestedLtc(getUsd().divide(getRequestBuyPrice()));
		setLastUsd(getUsd());
		setUsd(getUsd().subtract(getRequestBuyPrice().multiply(getRequestedLtc())));
		setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		}
		
		
	}
	
	//Make sure carrot sell price is more than buy price
	public void attemptSell(Carrot carrot) {
		if (getCurrentPrice().compareTo(carrot.getHigh().add(new BigDecimal("0.01"))) == -1) {
			setRequestSellPrice(carrot.getHigh().add(new BigDecimal("0.01")));
			if (getRequestSellPrice().compareTo(getRequestBuyPrice()) == 1) {
			
				setRequestedTotal(getRequestSellPrice().multiply(getLtc()));
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
			}
			
		}
	}
	
	public void broadcastCarrot(Carrot carrot) {
		setCurrentPrice(carrot.getCurrent());
		refresh();
	}
	
	
	public void refresh() {
		if(getBuyProcessState().equals("DESIRED_BUY")) {
			//if current price lower then desired buy then 
			//processBuy(which is buy()), store requested ltc
			//into ltc
			if(getCurrentPrice().compareTo(getRequestBuyPrice()) == -1) {
				buy();
			}
		} else if (getBuyProcessState().equals("DESIRED_SELL")) {
			if(getCurrentPrice().compareTo(getRequestSellPrice()) == 1) {
				sell();
			}
		}
	}
	
	//Ok, now to do sells (start in tradegroup)
	public void buy() {
		setLtc(getRequestedLtc());
		setBuyProcessState("BOUGHT");
	}
	
	public void sell() {
		setUsd(getRequestedTotal());
		setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		setLastUsd(getUsd());
		//profit percentage?
		setBuyProcessState("SOLD");
		setLifeTimeState("RESERVE");
	}
	//request... blah?

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

	public float getDesiredBuyTimeout() {
		return desiredBuyTimeout;
	}

	public void setDesiredBuyTimeout(float desiredBuyTimeout) {
		this.desiredBuyTimeout = desiredBuyTimeout;
	}

	public float getDesiredSellToStuckTimeout() {
		return desiredSellToStuckTimeout;
	}

	public void setDesiredSellToStuckTimeout(float desiredSellToStuckTimeout) {
		this.desiredSellToStuckTimeout = desiredSellToStuckTimeout;
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

	public BigDecimal getRequestedLtc() {
		return requestedLtc;
	}

	public void setRequestedLtc(BigDecimal requestedLtc) {
		this.requestedLtc = requestedLtc;
	}

	public BigDecimal getRequestedTotal() {
		return requestedTotal;
	}

	public void setRequestedTotal(BigDecimal requestedTotal) {
		this.requestedTotal = requestedTotal;
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
	
}
