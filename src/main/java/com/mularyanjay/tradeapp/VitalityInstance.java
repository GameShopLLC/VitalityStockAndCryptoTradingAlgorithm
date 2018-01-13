//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class VitalityInstance {

	private ArrayList<TradeGroup> groups;
	//Define entry point?
	private boolean hasReachedEntryPoint;
	private BigDecimal usd;
	private BigDecimal ltc;
	private BigDecimal profit;
	private String entryPointStatus;
	//poll entry point?
	//Use string to capture states?
	//Triggers? 
	
	//Initialize instance with list
	//...
	//....
	//Instance entry point
	//Instance stats (of all totals)
	//There needs to be data to make requests (Carrots??)
	
	public VitalityInstance() {
		
	}
	
	public VitalityInstance(BigDecimal initialUSD, TradeGroup... whatGroups) {
		
		setUsd(initialUSD);
		setLtc(new BigDecimal("0"));
		setProfit(new BigDecimal("0"));
		groups = new ArrayList<TradeGroup>();
		setEntryPointStatus("");
		for(TradeGroup g: whatGroups) {
			groups.add(g);
		}
	}
	public void onReachEntryPoint(ComparableDateTime cdt, BigDecimal price) {
		//System.out.println("Entry point reached " + cdt.toString() + " " + price);
		setEntryPointStatus("Entry point reached " + cdt.toString() + " " + price);
		System.out.println(getEntryPointStatus());
		//Should have logging system for vitality instance hooked to 
		//an ajax/restcontroller.  Maybe frontend also have variables
		//to manage logging phases
		
		//Checkpoints??  Class with variables but not full blown
		//instance?  ArrayList of checkpoints??
		
	}
	
	//Necessary? can be done with onReachEntryPoint
//	public void triggerEntryPoint() {
//		setHasReachedEntryPoint(true)
//	}
	
	//Maybe this will be the void that refreshes the whole
	//process
	
	//Data is broadcasted every (updated?) second keep that in mind
	public void broadcastCarrot(Carrot carrot) {
		for (TradeGroup g: groups) {
			g.broadcastCarrot(carrot);
		}
		//Check entry point(boolean? String?)
		if (!isHasReachedEntryPoint()) {
			for (TradeGroup g: groups) {
				if (g.getState().equals("ACTIVE")) {
					setHasReachedEntryPoint(true);
					onReachEntryPoint(carrot.getEndTime(), carrot.getCurrent());
					//deployThread?  Maybe thread will be deployed
					//before entry point log recognized.
					break;
				}
			}
		}
		
		updateBalance();
		//after cycle, checkTrade
	}
	
	//Will cycle to change balance..  Should be bigDecimal or have one
	public void updateBalance() {
		BigDecimal newUsd = new BigDecimal("0");
		BigDecimal newLtc = new BigDecimal("0");
		BigDecimal newProfit = new BigDecimal("0");
		for (TradeGroup g: groups) {
			newUsd = newUsd.add(g.getUsd());
			newLtc = newLtc.add(g.getLtc());
			newProfit = newProfit.add(g.getProfit());
		}
		setUsd(newUsd);
		setLtc(newLtc);
		setProfit(newProfit);
	}

	public String entryPointReport() {
		if (getEntryPointStatus().equals("")) {
			return "Searching entry point...";
		} else {
		return getEntryPointStatus();
		}
	}
	
	public String carrotsReport() {
		StringBuilder oneCarrots = new StringBuilder();
		StringBuilder fiveCarrots = new StringBuilder();
		StringBuilder tenCarrots = new StringBuilder();
		StringBuilder fifteenCarrots = new StringBuilder();
		StringBuilder thirtyCarrots = new StringBuilder();
		StringBuilder hourCarrots = new StringBuilder();
		
		oneCarrots.append("1) ");
		fiveCarrots.append("5) ");
		tenCarrots.append("10) ");
		fifteenCarrots.append("15) ");
		thirtyCarrots.append("30) ");
		hourCarrots.append("60) ");
		
		for (TradeGroup g: groups) {
			if (g.getName().contains("One")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				oneCarrots.append(g.getCurrentCarrot().toString());
				} else {
					oneCarrots.append("null");	
				}
			} 
			else if (g.getName().contains("Five")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				fiveCarrots.append(g.getCurrentCarrot().toString());
				} else {
					fiveCarrots.append("null");	
				}
			} 
			else if (g.getName().contains("Ten")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				tenCarrots.append(g.getCurrentCarrot().toString());
				} else {
					tenCarrots.append("null");	
				}
			} 
			else if (g.getName().contains("Fifteen")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				fifteenCarrots.append(g.getCurrentCarrot().toString());
				} else {
					fifteenCarrots.append("null");	
				}
			} 
			else if (g.getName().contains("Thirty")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				thirtyCarrots.append(g.getCurrentCarrot().toString());
				} else {
					thirtyCarrots.append("null");	
				}
			} 
			else if (g.getName().contains("Hour")) {
				//null check
				if (g.getCurrentCarrot() != null) {
				hourCarrots.append(g.getCurrentCarrot().toString());
				} else {
					hourCarrots.append("null");	
				}
			} 
		}
		
		return "" + oneCarrots.toString() + " " + fiveCarrots.toString() + " " + tenCarrots.toString() + " " + fifteenCarrots.toString() + " " + thirtyCarrots.toString() + " " + hourCarrots.toString();
	}
	//This will be detailed report
	//Sigh... what to put in report....
	//In all carrots report I will toString the current
	//carrots for each
	//Put current price, ltc, profits, highest growth tradegroup
	//profit from that tradegroup
	//Maybe this is as detailed as vi status report, but 
	//tradegroup status report will have information on threads
	//(more detailed)
	public String statusReport() {
		//need to calculate highest growth tradegroup
		TradeGroup highest = null;
		for (TradeGroup g: groups) {
			if (highest == null) {
				highest = g;
			} else {
				if (g.getProfit().compareTo(highest.getProfit()) == 1) {
					highest = g;
				}
			}
		}
		return "Current USD Balance: " + getUsd() + "\n" +
			   "Current Ltc Balance: " + getLtc() + "\n" +
			   "Current Profit: " + getProfit() + "\n" +
			   "Highest Growth TG: " + highest.getName() + "\n" +
			   "Highest TG Profit: " + highest.getProfit();
	}
	
	public TradeGroup searchTradeGroupByName(String name) {
		for (TradeGroup g: groups) {
			if (g.getName().equals(name)) {
				return g;
			}
		}
		return null;
	}
	
	public ArrayList<TradeGroup> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<TradeGroup> groups) {
		this.groups = groups;
	}

	public boolean isHasReachedEntryPoint() {
		return hasReachedEntryPoint;
	}

	public void setHasReachedEntryPoint(boolean hasReachedEntryPoint) {
		this.hasReachedEntryPoint = hasReachedEntryPoint;
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

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getEntryPointStatus() {
		return entryPointStatus;
	}

	public void setEntryPointStatus(String entryPointStatus) {
		this.entryPointStatus = entryPointStatus;
	}
	
	
}
