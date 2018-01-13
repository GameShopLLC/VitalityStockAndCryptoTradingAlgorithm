//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TradeGroup {

	private BigDecimal usd;
	private BigDecimal ltc;
	private ArrayList<TradeThread> trades;
	private String name;
	private String state; //STANDBY, ACTIVE
	private int amountThreads;
	private ArrayList<Carrot> carrotCache;
	private int carrotCacheNum;
	private int minuteTimeSpan;
	//start a search for entry point
	//*CHANGE TIMEOUTS TO LONG
	private long buyTimeout;
	private long stuckTimeout;
	private boolean hasReachedEntryPoint; //Delete?
	private Carrot currentCarrot;
	private BigDecimal profit;
	
	public TradeGroup() {
		
	}

	//*Need to apply timeouts*
	//Obviously, handling logging, statistical data
	
	public TradeGroup(String whatName, int whatAmountThreads, BigDecimal initialUSD, int timeSpan, int ccn, long bto, long sto) {
		//setHasReachedEntryPoint(false);
		setName(whatName);
		setAmountThreads(whatAmountThreads);
		setUsd(initialUSD);
		setLtc(new BigDecimal("0"));
		setMinuteTimeSpan(timeSpan);
		setCarrotCacheNum(ccn);
		carrotCache = new ArrayList<Carrot>();
		setState("STANDBY");
		trades = new ArrayList<TradeThread>();
		setBuyTimeout(bto);
		setStuckTimeout(sto);
		setProfit(new BigDecimal("0"));
		BigDecimal threadUSD = new BigDecimal(initialUSD.toString()).divide(BigDecimal.valueOf(getAmountThreads()));
		System.out.println("TradeGroup $" + getUsd() + " thread amount $" + threadUSD);
		for(int i = 0; i < getAmountThreads(); i++) {
			//BigDecimal initialUSD, float whatDBT, float whatDSTST) {
			trades.add(new TradeThread(threadUSD, getBuyTimeout(), getStuckTimeout()));
		}
		
	}
	
	//Deploy methods will handle attempts, refresh methods
	//will handle transactions (passing current carrot data).
	
	//Carrot param needed for low price?
	public void deployThread(Carrot carrot) {
		//sort to deploy right one on top
		//One thread at a time
		//What all should deployThread entail?
		//Do search throughout tradeThreads to see which
		//one is appropriate for buy
		int selectedIndex = 0; //??? ArrayList process of elimination?
		ArrayList<TradeThread> candidates = new ArrayList<TradeThread>();
		
		for (TradeThread t: trades) {
			if (t.getLifeTimeState().equals("RESERVE")) {
				candidates.add(t);
			}
		}
		if (candidates.size() == 0) {
			for (TradeThread t: trades) {
				if (t.getLifeTimeState().equals("IDLE")) {
					candidates.add(t);
				}
			}
		}
		//Now search through candidates and find most valuable
		for (TradeThread t: candidates) {
//			if (candidates.get(0).equals(t)) {
//				selectedIndex = 0; //redundant?
//			}
			if (candidates.get(selectedIndex).getUsd().compareTo(t.getUsd()) == -1) {
				selectedIndex = candidates.indexOf(t);
			}
			
		}
		//candidates
		for (TradeThread t: trades) {
			if (t.equals(candidates.get(selectedIndex))) {
				t.deploy(carrot); //Broadcast data needed
				break;
			}
		}
		
	}
	
	//With most ltc
	public void attemptSellThread(Carrot carrot) {
		int selectedIndex = 0;
		ArrayList<TradeThread> candidates = new ArrayList<TradeThread>();
		for (TradeThread t: trades) {
			if (t.getBuyProcessState().equals("BOUGHT")) {
				candidates.add(t);
				//TBC
			}
		}
		for (TradeThread t: candidates) {
//			if (candidates.get(0).equals(t)) {
//				selectedIndex = 0; //redundant?
//			}
			if (candidates.get(selectedIndex).getLtc().compareTo(t.getLtc()) == -1) {
				selectedIndex = candidates.indexOf(t);
			}
		}
		for (TradeThread t: trades) {
			if (t.equals(candidates.get(selectedIndex))) {
				t.attemptSell(carrot); //Broadcast data needed
				break;
			}
		}
	}
	
	//:D
	public void broadcastCarrot (Carrot carrot) {
		//Make "currentCarrot" variable and resize
		//carrot based on timespan????
		//A closed carrot for minute could still mean open
		//carrot for longer timespans.
		//meeeeehhhhh -____-
		//Also need to configure where group starts listening at
		//whole timespans
		//ex.. for 5 min, 10:00, 10:05, 10:10, 10:15
		//maybe add method cdt to handle that?
		//need to get current time without carrot?
		//broadcasted carrots are all minute carrottimes
		//simply make cdt method to capture exact second/minute/hour
		//times and compare to starttime(or endtime o.O, prob start)
		
		//Broadcast to thread second carrot.  Deploy with current
		//carrot/timespanCarrot
		
		//Problem solved ^.^
		//Move on to threads
		for (TradeThread t: trades) {
			t.broadcastCarrot(carrot); //or should I evaluate current
			//carrot and broadcast that? No, keep as is.
		}
		
		if(getName().contains("One")) {
			//if (getCurrentCarrot() == null) {
			setCurrentCarrot(carrot);
			if (!getCurrentCarrot().isActive()) {
				getCarrotCache().add(carrot);
				//doDeploy
				doDeploy();
			}
			//}
		} 
		else if (getName().contains("Five")) {
				if (carrot.getStartTime().slotEquals("minute", 0) ||
					carrot.getStartTime().slotEquals("minute", 5) ||	
					carrot.getStartTime().slotEquals("minute", 10) ||
					carrot.getStartTime().slotEquals("minute", 15) ||
					carrot.getStartTime().slotEquals("minute", 20) ||
					carrot.getStartTime().slotEquals("minute", 25) ||
					carrot.getStartTime().slotEquals("minute", 30) ||
					carrot.getStartTime().slotEquals("minute", 35) ||
					carrot.getStartTime().slotEquals("minute", 40) ||
					carrot.getStartTime().slotEquals("minute", 45) ||
					carrot.getStartTime().slotEquals("minute", 50) ||
					carrot.getStartTime().slotEquals("minute", 55)) {
					setCurrentCarrot(carrot);
				} else {
					if (getCurrentCarrot() != null) {
						//seconds getcurrenttime instead of startTime?
						if (getCurrentCarrot().getStartTime().increaseEquals(carrot.getCurrentTime(), "minute", 4) &&
								getCurrentCarrot().getStartTime().increaseGreater(carrot.getCurrentTime(), "second", new BigDecimal("58"))) {
							getCurrentCarrot().closeCarrot(carrot.getStartTime());
							getCarrotCache().add(getCurrentCarrot());
							doDeploy();
							setCurrentCarrot(null);
						} else {
							getCurrentCarrot().setActive(true);
							getCurrentCarrot().addCurrent(carrot.getCurrent());
						}
						//getCurrentCarr
						//set current all else but close at 4:59
					}
				}
		}
		else if (getName().contains("Ten")) {
			if (carrot.getStartTime().slotEquals("minute", 0) ||	
				carrot.getStartTime().slotEquals("minute", 10) ||
				carrot.getStartTime().slotEquals("minute", 20) ||
				carrot.getStartTime().slotEquals("minute", 30) ||
				carrot.getStartTime().slotEquals("minute", 40) ||
				carrot.getStartTime().slotEquals("minute", 50)) {
				setCurrentCarrot(carrot);
			} else {
				if (getCurrentCarrot() != null) {
					if (getCurrentCarrot().getStartTime().increaseEquals(carrot.getCurrentTime(), "minute", 9) &&
							getCurrentCarrot().getStartTime().increaseGreater(carrot.getCurrentTime(), "second", new BigDecimal("58"))) {
						getCurrentCarrot().closeCarrot(carrot.getStartTime());
						getCarrotCache().add(getCurrentCarrot());
						doDeploy();
						setCurrentCarrot(null);
					} else {
						getCurrentCarrot().setActive(true);
						getCurrentCarrot().addCurrent(carrot.getCurrent());
					}
					//getCurrentCarr
					//set current all else but close at 4:59
				}
			}
		}
		else if (getName().contains("Fifteen")) {
			if (carrot.getStartTime().slotEquals("minute", 0) ||
				carrot.getStartTime().slotEquals("minute", 15) ||
				carrot.getStartTime().slotEquals("minute", 30) ||
				carrot.getStartTime().slotEquals("minute", 45)) {
				setCurrentCarrot(carrot);
			} else {
				if (getCurrentCarrot() != null) {
					if (getCurrentCarrot().getStartTime().increaseEquals(carrot.getCurrentTime(), "minute", 14) &&
							getCurrentCarrot().getStartTime().increaseGreater(carrot.getCurrentTime(), "second", new BigDecimal("58"))) {
						getCurrentCarrot().closeCarrot(carrot.getStartTime());
						getCarrotCache().add(getCurrentCarrot());
						doDeploy();
						setCurrentCarrot(null);
					} else {
						getCurrentCarrot().setActive(true);
						getCurrentCarrot().addCurrent(carrot.getCurrent());
					}
					//getCurrentCarr
					//set current all else but close at 4:59
				}
			}
		}
		else if (getName().contains("Thirty")) {
			if (carrot.getStartTime().slotEquals("minute", 0) ||
				carrot.getStartTime().slotEquals("minute", 30)) {
				setCurrentCarrot(carrot);
			} else {
				if (getCurrentCarrot() != null) {
					if (getCurrentCarrot().getStartTime().increaseEquals(carrot.getCurrentTime(), "minute", 29) &&
							getCurrentCarrot().getStartTime().increaseGreater(carrot.getCurrentTime(), "second", new BigDecimal("58"))) {
						getCurrentCarrot().closeCarrot(carrot.getStartTime());
						getCarrotCache().add(getCurrentCarrot());
						doDeploy();
						setCurrentCarrot(null);
					} else {
						getCurrentCarrot().setActive(true);
						getCurrentCarrot().addCurrent(carrot.getCurrent());
					}
					//getCurrentCarr
					//set current all else but close at 4:59
				}
			}
		}
		else if (getName().contains("Hour")) {
			if (carrot.getStartTime().slotEquals("minute", 0)) {
				setCurrentCarrot(carrot);
			} else {
				if (getCurrentCarrot() != null) {
					if (getCurrentCarrot().getStartTime().increaseEquals(carrot.getCurrentTime(), "minute", 59) &&
							getCurrentCarrot().getStartTime().increaseGreater(carrot.getCurrentTime(), "second", new BigDecimal("58"))) {
						getCurrentCarrot().closeCarrot(carrot.getStartTime());
						getCarrotCache().add(getCurrentCarrot());
						doDeploy();
						setCurrentCarrot(null);
					} else {
						getCurrentCarrot().setActive(true);
						getCurrentCarrot().addCurrent(carrot.getCurrent());
					}
					//getCurrentCarr
					//set current all else but close at 4:59
				}
			}
		}
		
		
//		//Dont forget about carrotCache (the point of
//		//currentCarrot
//		//Also have to count the time in greater than 1 minute
//		if (getCarrotCache().size() == 0) {
//		
//			if (!carrot.isActive()) {
//				getCarrotCache().add(carrot);
//			}
//			
//		} else {
//			//Will this be useful when there is currentCarrot?
//			//Probably not, but the code will be renovated in
//			//upper clause to better fit situation 
//			//if getname
//			if (getName().contains("One")) {
//				if (!carrot.isActive()) {
//					getCarrotCache().add(carrot);
//				}
//				// else assess carrot? -_-
//				
//			} else if (getName().contains("Five")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(getCarrotCache().get(getCarrotCache().size() - 1).getStartTime(), "minute", 5)) {
//					getCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Ten")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(getCarrotCache().get(getCarrotCache().size() - 1).getStartTime(), "minute", 10)) {
//					getCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Fifteen")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(getCarrotCache().get(getCarrotCache().size() - 1).getStartTime(), "minute", 15)) {
//					getCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Thirty")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(getCarrotCache().get(getCarrotCache().size() - 1).getStartTime(), "minute", 30)) {
//					getCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Hour")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(getCarrotCache().get(getCarrotCache().size() - 1).getStartTime(), "minute", 60)) {
//					getCarrotCache().add(carrot);
//				}
//			}
				
			//end if..
			
			//Use single dip instead of double dip for all
			//*BE SURE TO CHANGE*
			//I could be fucking up
			//Timespans larger than a minute should have a 
			//larger carrot.
			//Merge carrots method?
			//Or instead of waiting, actively resize carrot and
			//input into all groups concurrently
			//Carrot reshaping could be handled in tradegroup
			//instead of frontend (or with frontend).
			
		
			
//			switch(getName()) {
//				
//			}
			updateBalance();
		}
	
	
	//Threads need to make request through hierarchy so that
	//there will be one thread at a time making trade (within a group)
	
	
	//Change this to check all buy and sold states
//	public BigDecimal checkTrade() {
//		return new BigDecimal("0");
//	}
	
	public void doDeploy() {
		//Add triggerDeploy method
		//Ok, first change to single dip
		if (getState().equals("STANDBY")) {
			if (getCarrotCache().size() > 0) {
//				if (getName().contains("One")) {
//					if (getCarrotCache().size() > 1) {
//						//if... hit entry point
//						if (getCarrotCache().get(getCarrotCache().size() - 1).getTrend().equals("DEC") && getCarrotCache().get(getCarrotCache().size() - 2).getTrend().equals("DEC")) {
//							setState("ACTIVE");		
//							deployThread(getCarrotCache().get(getCarrotCache().size() - 1)); //well?
//						}
//					}
//				}
				//else {
					//if (getCarrotCache().size() > 0) {
						//if... hit entry point
						if (getCarrotCache().get(getCarrotCache().size() - 1).getTrend().equals("DEC")) {
							setState("ACTIVE");	
							deployThread(getCarrotCache().get(getCarrotCache().size() - 1));
						}
						//INC for buy... but not on standby, only on ACTIVE
					//}
				//}
			}
		}
		else if (getState().equals("ACTIVE")) {
			if (getCarrotCache().size() > 0) {
//				if (getName().contains("One")) {
//					if (getCarrotCache().size() > 1) {
//						//if... hit entry point
//						if (getCarrotCache().get(getCarrotCache().size() - 1).getTrend().equals("DEC") && getCarrotCache().get(getCarrotCache().size() - 2).getTrend().equals("DEC")) {
//							//setState("ACTIVE");		
//							deployThread(getCarrotCache().get(getCarrotCache().size() - 1));
//						}
//					}
//				}
//				else {
//					if (getCarrotCache().size() > 0) {
						//if... hit entry point
						if (getCarrotCache().get(getCarrotCache().size() - 1).getTrend().equals("DEC")) {
							//setState("ACTIVE");
							deployThread(getCarrotCache().get(getCarrotCache().size() - 1));
						} 
						else if (getCarrotCache().get(getCarrotCache().size() - 1).getTrend().equals("INC")) {
							//setState("ACTIVE");
							//deployThread(getCarrotCache().get(getCarrotCache().size() - 1));
							attemptSellThread(getCarrotCache().get(getCarrotCache().size() - 1));
						}
						//*****INC for buy since ACTIVE here*****
//					}
//				}
			}
		}
	}
	
	public void updateBalance() {
		BigDecimal newUsd = new BigDecimal("0");
		BigDecimal newLtc = new BigDecimal("0");
		BigDecimal newProfit = new BigDecimal("0");
		for (TradeThread t: trades) {
			newUsd = newUsd.add(t.getUsd());
			newLtc = newLtc.add(t.getLtc());
			newProfit = newProfit.add(t.getProfit());
		}
		setUsd(newUsd);
		setLtc(newLtc);
		setProfit(newProfit);
	}
	
	//Amount buy stuck threads
	//amount sell stuck threads
	//Amount idle threads
	//Amount active threads
	//Amount buying threads
	//Amount selling threads
	//Strongest thread usd/ltc/profit (measured by profit?)
	public String statusReport() {
		int idleThreadCount = 0;
		int activeThreadCount = 0;
		int buyingThreadCount = 0;
		int sellingThreadCount = 0;
		int buyStuckCount = 0;
		int sellStuckCount = 0;
		TradeThread highest = null;
		for (TradeThread t: trades) {
			if (t.getLifeTimeState().equals("IDLE")) {
				idleThreadCount++;
			} else {
				activeThreadCount++;
			}
			if (t.getBuyProcessState().equals("DESIRED_BUY")) {
				buyingThreadCount++;
			} else if(t.getBuyProcessState().equals("BOUGHT") || t.getBuyProcessState().equals("DESIRED_SELL")) {
				sellingThreadCount++;
			}
			if (t.getLifeTimeState().equals("BUY_STUCK")) {
				buyStuckCount++;
			} else if(t.getLifeTimeState().equals("SELL_STUCK")) {
				sellStuckCount++;
			}
			if (highest == null) {
				highest = t;
			} else {
				if (t.getProfit().compareTo(highest.getProfit()) == 1) {
					highest = t;
				}
			}
			//sell is bought or desired sell
		}
//		int idleThreadCount = 0;
//		int activeThreadCount = 0;
//		int buyingThreadCount = 0;
//		int sellingThreadCount = 0;
//		int buyStuckCount = 0;
//		int sellStuckCount = 0;
		return "Current USD Balance: " + getUsd() + "\n" +
			   "Current Ltc Balance: " + getLtc() + "\n" +
			   "Current Profit: " + getProfit() + "\n" +
			   "Idle Threads: " + idleThreadCount + "\n" +
			   "Active Threads: " + activeThreadCount + "\n" +
			   "Buying threads: " + buyingThreadCount + "\n" +
			   "Selling threads: " + sellingThreadCount + "\n" +
			   "Stuck buying: " + buyStuckCount + "\n" +
			   "Stuck selling: " + sellStuckCount + "\n" +
			   "Strongest thread USD: " + highest.getUsd() + "\n" +
			   "Strongest thread LTC: " + highest.getLtc() + "\n" +
			   "Strongest thread profit: " + highest.getProfit();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ArrayList<TradeThread> getTrades() {
		return trades;
	}

	public void setTrades(ArrayList<TradeThread> trades) {
		this.trades = trades;
	}

	public int getAmountThreads() {
		return amountThreads;
	}

	public void setAmountThreads(int amountThreads) {
		this.amountThreads = amountThreads;
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

	public ArrayList<Carrot> getCarrotCache() {
		//if(carrotCache != null)
		if (carrotCache.size() > getCarrotCacheNum()) {
			carrotCache.remove(0);
		}
		return carrotCache;
	}

	public void setCarrotCache(ArrayList<Carrot> carrotCache) {
		this.carrotCache = carrotCache;
	}

	public int getCarrotCacheNum() {
		return carrotCacheNum;
	}

	public void setCarrotCacheNum(int carrotCacheNum) {
		this.carrotCacheNum = carrotCacheNum;
	}

	public int getMinuteTimeSpan() {
		return minuteTimeSpan;
	}

	public void setMinuteTimeSpan(int minuteTimeSpan) {
		this.minuteTimeSpan = minuteTimeSpan;
	}

	public long getBuyTimeout() {
		return buyTimeout;
	}

	public void setBuyTimeout(long buyTimeout) {
		this.buyTimeout = buyTimeout;
	}

	public long getStuckTimeout() {
		return stuckTimeout;
	}

	public void setStuckTimeout(long stuckTimeout) {
		this.stuckTimeout = stuckTimeout;
	}

	public boolean isHasReachedEntryPoint() {
		return hasReachedEntryPoint;
	}

	public void setHasReachedEntryPoint(boolean hasReachedEntryPoint) {
		this.hasReachedEntryPoint = hasReachedEntryPoint;
	}

	public Carrot getCurrentCarrot() {
		return currentCarrot;
	}

	public void setCurrentCarrot(Carrot currentCarrot) {
		this.currentCarrot = currentCarrot;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	
	
}
