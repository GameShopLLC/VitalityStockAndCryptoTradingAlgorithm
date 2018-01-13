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
		groups = new ArrayList<TradeGroup>();
		
		for(TradeGroup g: whatGroups) {
			groups.add(g);
		}
	}
	public void onReachEntryPoint(ComparableDateTime cdt, BigDecimal price) {
		System.out.println("Entry point reached " + cdt.toString() + " " + price);
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
		
		//after cycle, checkTrade
	}
	
	//Will cycle to change balance..  Should be bigDecimal or have one
	public void checkTrade() {
		
	}

	//This will be detailed report
	public String statusReport() {
		return "To Do";
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
	
	
}
