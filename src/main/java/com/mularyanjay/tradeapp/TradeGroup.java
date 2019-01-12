//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.persistence.Entity;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.AccessType.Type;

//@Document
@AccessType(Type.PROPERTY)
public class TradeGroup {

	
	private BigDecimal usd;
	private BigDecimal ltc;
	private ArrayList<TradeThread> trades;
	private String name;
	private String state; //STANDBY, ACTIVE, RALLYING
	private String runningState; //GOING, STOPPED
	private int amountThreads;
	// @AccessType(Type.FIELD)
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
	
	private ArrayList<Dock> docks;
	private BigDecimal loss;
	private BigDecimal net;
	private int step; //how many temporary maximums were there
	private int maxStep; //maximum amount of temporary maximums
	private BigDecimal stepTotal; //temporary maximum of thread size
	private String stepMode; //NONE, STEPSHED
	private SimulationMode simMode;//SIMULATION, REALTIME
	
	@Transient
	private int idleThreadCount;
	@Transient
	private int activeThreadCount;
	@Transient
	private int buyingThreadCount;
	@Transient
	private int sellingThreadCount;
	@Transient
	private int buyStuckCount;
	@Transient
	private int sellStuckCount;
	private String dumpingMode; //NONE, DUMP_ALL
	private String lossMode; //NONE, IMMEDIATE, SPLIT, INSTANT
	private String splitMode;//NONE, ZENO_CLASSIC, ZENO_LOCK_AND_RALLY
	private int splitNum;
	private long forceLossTimeout;
	private BigDecimal accountSnapshot;
	private BigDecimal fee;
	private Timer timer;
	private long tick;
	@Transient
	private ArrayList<String> acceptedLossLog;
	private String forceLossMode;//NONE, ACCEPTED_LOSS, NEGATIVE_LOSS
	private BigDecimal slightAmount;
	//forceLossagent
	//private String sellingMode; //NONE, IMMEDIATESELL
	//private int steppedThreads make local
	public TradeGroup() {
		
	}

	//*Need to apply timeouts*
	//Obviously, handling logging, statistical data
	
	public TradeGroup(SimulationMode sm, String whatName, String stepMode, int whatAmountThreads, BigDecimal initialUSD, int timeSpan, int ccn, long bto, long sto, long flto, BigDecimal slightAmount) {
		//setHasReachedEntryPoint(false);
		//setSimMode(new String("SIMULATION"));
		setSlightAmount(slightAmount);
		setForceLossMode("NONE");
		acceptedLossLog = new ArrayList<String>();
		setFee(new BigDecimal("0.003"));
		setAccountSnapshot(initialUSD);
		setForceLossTimeout(flto);
		setSplitMode(new String("NONE"));  //if contains zeno
		setLossMode(new String("IMMEDIATE")); //IMMEDIATE
		setDumpingMode(new String("DUMP_ALL"));
		setSimMode(sm);
		setLoss(new BigDecimal("0"));
		setNet(new BigDecimal("0"));
		setStepTotal(new BigDecimal("0"));
		setStepMode(stepMode);
		setStep(0);
		setMaxStep(2);
		setName(whatName);
		setAmountThreads(whatAmountThreads);
		setUsd(initialUSD);
		setLtc(new BigDecimal("0"));
		setMinuteTimeSpan(timeSpan);
		setCarrotCacheNum(ccn);
		carrotCache = new ArrayList<Carrot>();
		setState("STANDBY");
		setRunningState("GOING");
		trades = new ArrayList<TradeThread>();
		setBuyTimeout(bto);
		setStuckTimeout(sto);
		setProfit(new BigDecimal("0"));
		docks = new ArrayList<Dock>();
		docks.add(new Dock("REMAINDER"));
		docks.add(new Dock("STEPSHED"));
		docks.add(new Dock("TOPROFIT"));
		
		setSplitNum(getAmountThreads() * 2 / 3);
		partitionThreads();
		
	}
	
	public void startTimers() {
		for (TradeThread t: trades) {
			t.startTimer();
		}
	}
	public boolean acceptLoss() {
		BigDecimal forcedAmount = new BigDecimal("0");
		forcedAmount = ((getCurrentCarrot().getCurrent().subtract(getCurrentCarrot().getCurrent().multiply(getFee()))).multiply(getLtc()));
		//updateBalance();
		if ((forcedAmount.add(getUsd())).compareTo(getAccountSnapshot()) == 1) {
			System.out.println("Accepted Loss");
			getAcceptedLossLog().add("Accepted Loss at: " + getCurrentCarrot().getCurrentTime().toString() + " " + forcedAmount);
			return true;
		}
		System.out.println("Loss Unacceptable");
		getAcceptedLossLog().add("Loss unacceptable at: " + getCurrentCarrot().getCurrentTime().toString() + " " + forcedAmount);
		return false;
	}
	public void shedFromOutgoingToStep() {
		
		for(TradeThread t: trades) {
		
			if(t.getDockByName("TOSTEPSHED").getUsd().compareTo(new BigDecimal("0")) == 1) {
				getDockByName("STEPSHED").addUsd(t.getDockByName("TOSTEPSHED").getUsd());
				t.getDockByName("TOSTEPSHED").setUsd(new BigDecimal("0"));
			}
			
		}
	}
	
	public void shedFromThreadsToProfit() {
		
	}
	
	public void shedFromRemainderToStep() {
		
	}
	
	public void partitionThreads() {
		//getUsd was initialUsd
		BigDecimal threadUSD = new BigDecimal(getUsd().toString()).divide(BigDecimal.valueOf(getAmountThreads()), 2, RoundingMode.FLOOR);
		
		System.out.println("TradeGroup $" + getUsd() + " thread amount $" + threadUSD);
		for(int i = 0; i < getAmountThreads(); i++) {
			//BigDecimal initialUSD, float whatDBT, float whatDSTST) {
			trades.add(new TradeThread(getSimMode(), threadUSD, getBuyTimeout(), getStuckTimeout(), getSlightAmount()));
		}
		
		setStepTotal(threadUSD.multiply(new BigDecimal("10")));
		BigDecimal total = threadUSD.multiply(BigDecimal.valueOf(getAmountThreads()));
		BigDecimal remainder = getUsd().subtract(total);
		getDockByName("REMAINDER").addUsd(remainder);
		System.out.println("Step Total: " + getStepTotal());
		System.out.println("Remainder: " + remainder);
		
		for (TradeThread t: trades) {
			//t.setSimMode(getSimMode());
			t.setStepTotal(getStepTotal());
		}
	}
	
	public Dock getDockByName(String name) {
		for (Dock d: docks) {
			if (d.getName().equals(name)) {
				return d;
			}
		}
		System.out.println("No such dock with that name");
		return null;
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
		
		//if (getDumpingMode().equals("NONE")) {
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
		
		if (candidates.size() == 0) {
			System.out.println("No threads available");
			return;
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
	//} else if (getDumpingMode().equals("DUMP_ALL")) {
		
	//}
	}
	
	//With most ltc
	public void attemptSellThread(Carrot carrot) {
		int selectedIndex = 0;
		ArrayList<TradeThread> candidates = new ArrayList<TradeThread>();
		if (getDumpingMode().equals("NONE")) {
		for (TradeThread t: trades) {
			if (t.getBuyProcessState().equals("BOUGHT") && !t.getLifeTimeState().equals("SELL_STUCK")) {
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
		
		if (candidates.size() == 0) {
			System.out.println("No threads available");
			return;
		}
		
		for (TradeThread t: trades) {
			if (t.equals(candidates.get(selectedIndex))) {
				t.attemptSell(carrot); //Broadcast data needed
				break;
			}
		}
		} else if (getDumpingMode().equals("DUMP_ALL")) {
			for (TradeThread t: trades) {
				if (t.getBuyProcessState().equals("BOUGHT") && !t.getLifeTimeState().equals("SELL_STUCK")) {
					candidates.add(t);
					//TBC
				}
			}
			
			if (candidates.size() == 0) {
				System.out.println("No threads available");
				return;
			}
			
			for (TradeThread t: candidates) {
				//if (t.equals(candidates.get(selectedIndex))) {
					t.attemptSell(carrot); //Broadcast data needed
					//break;
				//}
			}
		}
	}
	
	public void performSplit() {
		//ArrayList<TradeThread> halvedTrades = new ArrayList<TradeThread>();
		int idleTrades = getIdleThreadCount();
		BigDecimal idleUsd = new BigDecimal("0");
		ArrayList<BigDecimal> idleNums = new ArrayList<BigDecimal>();
		for (TradeThread t: trades) {
			if (t.getLifeTimeState().equals("IDLE")) {
				idleUsd = t.getUsd().divide(new BigDecimal("2"), 8, RoundingMode.FLOOR);
				//break;
				idleNums.add(new BigDecimal(idleUsd.toString()));
			}
			//TradeThread temp = t.
			//MUST MAKE COPY CONSTRUCTOR 
		}
//		List<String> names = ....
//				Iterator<String> i = names.iterator();
//				while (i.hasNext()) {
//				   String s = i.next(); // must be called before you can call i.remove()
//				   // Do something
//				   i.remove();
//				}
		Iterator<TradeThread> i = trades.iterator();
		while (i.hasNext()) {
			TradeThread t = i.next();
			if (t.getLifeTimeState().equals("IDLE")) {
			i.remove();
			}
		}
		
		for (int j = 0; j < idleTrades; j++) {
			trades.add(new TradeThread(getSimMode(), idleNums.get(j), getBuyTimeout(), getStuckTimeout(), getSlightAmount()));
			trades.add(new TradeThread(getSimMode(), idleNums.get(j), getBuyTimeout(), getStuckTimeout(), getSlightAmount()));
			
		}
//		for (TradeThread t: trades) {
//			if (t.getLifeTimeState().equals("IDLE")) {
//				trades.remove(t);
//			}
//		}
	}
	public void checkSplit() {
		if (getActiveThreadCount() >= getSplitNum()) {
			 performSplit();
			 setSplitNum((getActiveThreadCount() + getIdleThreadCount()) * 2 / 3);
		}
		System.out.println("Split num: " + getSplitNum());
		System.out.println("Active threads:" + getActiveThreadCount());
		System.out.println("Idle threads:" + getIdleThreadCount());
		
		//split num must be new number num + 2/3
	}
	
	public void forceLoss() {
		for (TradeThread t: trades) {
			t.forceLoss();
		}
		updateBalance();
		setAccountSnapshot(getUsd());
	}

	public void correctThreads(){
		
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
		//if (getSimMode().equals("REALTIME")) {
		// for (TradeThread t: trades) {
		// 		if (t.getBuyProcessState().equals("SUSPEND")){
		// 			if (carrot.getCurrent().compareTo(t.getRequestBuyPrice()) <= 0) {
		// 				t.cancelBuy();
		// 			} else {
		// 				t.setBuyProcessState(new String("DESIRED_BUY"));
		// 			}
		// 		}
		// 	}
				

			// }


//		} else if (getSimMode().equals("SIMULATION")) {
//			for (TradeThread t: trades) {
//				t.refresh();
//			}
//		}
		for (TradeThread t: trades) {
			t.broadcastCarrot(carrot); //or should I evaluate current
			//carrot and broadcast that? No, keep as is.
		}
			
		//CHECK SPLIT
		if (getSplitMode().equals("ZENO_CLASSIC")) {
			System.out.println("Checking split");
			checkSplit();
		}
		
		//PUT FORCELOSS LOGIC HERE.  NEEDS TIMEOUT
		if (!getForceLossMode().equals("NONE")){
		if (getSimMode() == SimulationMode.REALTIME) {
			setTimer(new Timer());
			getTimer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (getForceLossMode().equals("ACCEPTED_LOSS")) {
					if (acceptLoss()) {
						forceLoss();
					}
					} else if (getForceLossMode().equals("NEGATIVE_LOSS")) {
						forceLoss();
					}
				}
				
			}, getForceLossTimeout(), getForceLossTimeout());
		} else if (getSimMode() == SimulationMode.SIMULATION) {
			if (getTick() >= getForceLossTimeout()/1000) {
				if (getForceLossMode().equals("ACCEPTED_LOSS")) {
				if (acceptLoss()) {
					forceLoss();
				}
				}  else if (getForceLossMode().equals("NEGATIVE_LOSS")) {
					forceLoss();
				}
				setTick(0L);
			}
		}
	}
		//...
		if(getName().contains("One")) {
			//if (getCurrentCarrot() == null) {
			setCurrentCarrot(carrot);
			if (!getCurrentCarrot().isActive()) {
				useCarrotCache().add(carrot);
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
							useCarrotCache().add(getCurrentCarrot());
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
						useCarrotCache().add(getCurrentCarrot());
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
						useCarrotCache().add(getCurrentCarrot());
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
						useCarrotCache().add(getCurrentCarrot());
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
						useCarrotCache().add(getCurrentCarrot());
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
		
		// for (TradeThread t: trades){
		// 			// t.setSecondTick(t.getSecondTick() + 100L);
		// 		if (t.getLifeTimeState().equals("BUY_STUCK")) {
		// 			t.cancelBuy();
		// 			// buystuck = true;
		// 		}	
		// 		}

//***********************REMEMBER*********************************
// if (getLossMode().equals("IMMEDIATE")) {
// 			boolean sellstuck = false;

// 			// for (TradeThread t: trades) {
// 			// 	if (t.getLifeTimeState().equals("SELL_STUCK")) {
// 			// 	// 	for (TradeThread b: trades){
// 			// 	// 	if (b.getBuyProcessState().equals("DESIRED_BUY")){
// 			// 	// 	b.setBuyProcessState(new String("SUSPEND"));
// 			// 	// }
// 			// 	//}
// 			// 		// if (t.cancelOrder().contains(t.getOrderId())){
// 			// 		if (t.getBuyProcessState().equals("DESIRED_SELL")){
// 			// 			if (t.getOrderId() != null){
// 			// 			if (t.getActiveOrder().getSettled() == false){
// 			// 				// t.cancelOrder();
// 			// 				sellstuck = true;
							
// 			// 			}
// 			// 			}
// 			// 		}
					
						
						
// 			// 		// } else {
// 			// 		// 	System.out.println("Failed to cancel")
// 			// 		// }
// 			// 		 //forceSell?
					
// 			// 	}
// 			// }

// 		// 	if (sellstuck){
// 		// 	try {
// 		// 	Thread.sleep(100);
// 		// } catch (InterruptedException e) {
// 		// 	// TODO Auto-generated catch block
// 		// 	e.printStackTrace();
// 		// }

				

// 				for (TradeThread t: trades){
// 					// t.setSecondTick(t.getSecondTick() + 100L);
// 				if (t.getLifeTimeState().equals("SELL_STUCK")) {
// 					if (t.getPartialState().equals("NONE")){
// 						 t.forceLoss();
// 						 // return;
// 						//t.attemptSell(carrot);
// 					} else if (t.getPartialState().equals("PARTIAL")){
// 						t.sellPartial();
// 						// return;
// 					}
					
// 				}
// 			}
// 		// }
// 			// for (TradeThread t: trades) {
// 			// 	if (t.getBuyProcessState().equals("SUSPEND")){
// 			// 		if (getCurrentCarrot().getCurrent().compareTo(t.getRequestBuyPrice()) <= 0) {
// 			// 			t.cancelBuy();
// 			// 		} else {
// 			// 			t.setBuyProcessState(new String("DESIRED_BUY"));
// 			// 		}
// 			// 	}
// 			// }
// 			} else if (getLossMode().equals("INSTANT")) {
// 				boolean sellAll = false;
// 				for (TradeThread t: trades) {
// 					if (t.getLifeTimeState().equals("SELL_STUCK")) {
// 						sellAll = true;
// 						break;
// 					}
					
// 				}
				
// 				for (TradeThread t: trades) {
// 					if (t.getBuyProcessState().equals("BOUGHT") || t.getBuyProcessState().equals("DESIRED_SELL")) {
// 						t.forceSell();
// 					}
// 				}
// 			}
				
		

		
//		//Dont forget about carrotCache (the point of
//		//currentCarrot
//		//Also have to count the time in greater than 1 minute
//		if (useCarrotCache().size() == 0) {
//		
//			if (!carrot.isActive()) {
//				useCarrotCache().add(carrot);
//			}
//			
//		} else {
//			//Will this be useful when there is currentCarrot?
//			//Probably not, but the code will be renovated in
//			//upper clause to better fit situation 
//			//if getname
//			if (getName().contains("One")) {
//				if (!carrot.isActive()) {
//					useCarrotCache().add(carrot);
//				}
//				// else assess carrot? -_-
//				
//			} else if (getName().contains("Five")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(useCarrotCache().get(useCarrotCache().size() - 1).getStartTime(), "minute", 5)) {
//					useCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Ten")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(useCarrotCache().get(useCarrotCache().size() - 1).getStartTime(), "minute", 10)) {
//					useCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Fifteen")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(useCarrotCache().get(useCarrotCache().size() - 1).getStartTime(), "minute", 15)) {
//					useCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Thirty")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(useCarrotCache().get(useCarrotCache().size() - 1).getStartTime(), "minute", 30)) {
//					useCarrotCache().add(carrot);
//				}
//			} else if (getName().contains("Hour")) {
//				if (!carrot.isActive() && carrot.getStartTime().increaseEquals(useCarrotCache().get(useCarrotCache().size() - 1).getStartTime(), "minute", 60)) {
//					useCarrotCache().add(carrot);
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
			//if (getSimMode() == SimulationMode)
			
			if (getSimMode() == SimulationMode.SIMULATION) {
				setTick(getTick() + 60L);
			for (TradeThread t: trades) {
				t.incrementSecondTick(60L);
				//t.evaluateSimulationTimeout();
			}
			}
		// 	boolean buystuck = false;
		// 	for (TradeThread t: trades){
		// 		if (t.getLifeTimeState().equals("BUY_STUCK")) {
		// 			// t.cancelOrder();
		// 			buystuck = true;
		// 		}
		// 	}

		// 	if (buystuck){
		// 		//buystuck = false;
		// 		try {
		// 		Thread.sleep(100);
		// 		}	catch (InterruptedException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }

			for (TradeThread t: trades) {
				t.calculateNet();
			}
		
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
		if (getRunningState().equals("GOING")) {
		if (getState().equals("STANDBY")) {
			if (useCarrotCache().size() > 0) {
//				if (getName().contains("One")) {
//					if (useCarrotCache().size() > 1) {
//						//if... hit entry point
//						if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC") && useCarrotCache().get(useCarrotCache().size() - 2).getTrend().equals("DEC")) {
//							setState("ACTIVE");		
//							deployThread(useCarrotCache().get(useCarrotCache().size() - 1)); //well?
//						}
//					}
//				}
				//else {
					//if (useCarrotCache().size() > 0) {
						//if... hit entry point
						if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend() != null && useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC") || useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("EQ")) {
							setState("ACTIVE");	
							deployThread(useCarrotCache().get(useCarrotCache().size() - 1));
						}
						//INC for buy... but not on standby, only on ACTIVE
					//}
				//}
			}
		}
		else if (getState().equals("ACTIVE")) {
			if (useCarrotCache().size() > 0) {
//				if (getName().contains("One")) {
//					if (useCarrotCache().size() > 1) {
//						//if... hit entry point
//						if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC") && useCarrotCache().get(useCarrotCache().size() - 2).getTrend().equals("DEC")) {
//							//setState("ACTIVE");		
//							deployThread(useCarrotCache().get(useCarrotCache().size() - 1));
//						}
//					}
//				}
//				else {
//					if (useCarrotCache().size() > 0) {
						//if... hit entry point
				if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend() != null){
						if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC") || useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("EQ")) {
							//setState("ACTIVE");
							deployThread(useCarrotCache().get(useCarrotCache().size() - 1));
						} 
						 if (useCarrotCache().get(useCarrotCache().size() - 1).getTrend() != null && (useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("INC") || useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("EQ")) ) { //|| useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC") ) {
							//setState("ACTIVE");
							//deployThread(useCarrotCache().get(useCarrotCache().size() - 1));
							attemptSellThread(useCarrotCache().get(useCarrotCache().size() - 1));
						}
					}
						//*****INC for buy since ACTIVE here*****
//					}
//				}
			}
		} else if (getState().equals("RALLYING")) {
			if (useCarrotCache().size() > 0) {
				if(useCarrotCache().get(useCarrotCache().size() - 1).getTrend() != null ){
				if ((useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("INC") || useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("EQ")) ) { //|| useCarrotCache().get(useCarrotCache().size() - 1).getTrend().equals("DEC")) {
					//setState("ACTIVE");
					//deployThread(useCarrotCache().get(useCarrotCache().size() - 1));
					attemptSellThread(useCarrotCache().get(useCarrotCache().size() - 1));
				}
			}
			}
		}
		if (getSimMode() == SimulationMode.SIMULATION) {
			for (TradeThread t: trades) {
				t.incrementSecondTick(60L);
			}
		}
		checkDoneRallying();
	}
	
	}
	
	public void checkDoneRallying() {
		if (getState().equals("RALLYING")) {
			for(TradeThread t: trades) {
				if (!t.getBuyProcessState().equals("SOLD")) {
					break;
				} else {
					setState("STANDBY");
					setRunningState("STOPPED");
					//need to set to standby,
					//need a stop and go state
				}
			}
		}
	}
	public void updateBalance() {
		BigDecimal newUsd = new BigDecimal("0");
		BigDecimal newLtc = new BigDecimal("0");
		BigDecimal newProfit = new BigDecimal("0");
		BigDecimal newLoss = new BigDecimal("0");
		BigDecimal newNet = new BigDecimal("0");
		
		for (TradeThread t: trades) {
			newUsd = newUsd.add(t.getUsd());
			newLtc = newLtc.add(t.getLtc());
			newProfit = newProfit.add(t.getProfit());
			newLoss = newLoss.add(t.getLoss());
			newNet = newNet.add(t.getNet());
		}
		setUsd(newUsd);
		setLtc(newLtc);
		setProfit(newProfit);
		setLoss(newLoss);
		setNet(newNet);
	}
	
	//Amount buy stuck threads
	//amount sell stuck threads
	//Amount idle threads
	//Amount active threads
	//Amount buying threads
	//Amount selling threads
	//Strongest thread usd/ltc/profit (measured by profit?)
	public String statusReport() {
		
		TradeThread highest = null;
//		StringBuilder acceptedLossStatus = new StringBuilder();
//		for (String s: acceptedLossLog) {
//			acceptedLossStatus.append(s + "\n");
//		}
		for (TradeThread t: trades) {
			
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
		return 	"State: " + getState() + "\n" +
				"Running State: " + getRunningState() + "\n" +
				"Current Time: " + getCurrentCarrot().getCurrentTime() + "\n" +
				"Current Price: " + getCurrentCarrot().getCurrent() + "\n" +
				"Current USD Balance: " + getUsd() + "\n" +
			   "Current Ltc Balance: " + getLtc() + "\n" +
			   "Current Profit: " + getProfit() + "\n" +
			   "Current Loss: " + getLoss() + "\n" +
			   "Current Net: " + getNet() + "\n" +
			   "Idle Threads: " + getIdleThreadCount() + "\n" +
			   "Active Threads: " + getActiveThreadCount() + "\n" +
			   "Buying threads: " + getBuyingThreadCount() + "\n" +
			   "Selling threads: " + getSellingThreadCount() + "\n" +
			   "Stuck buying: " + getBuyStuckCount() + "\n" +
			   "Stuck selling: " + getSellStuckCount() + "\n" +
			   "Strongest thread USD: " + highest.getUsd() + "\n" +
			   "Strongest thread LTC: " + highest.getLtc() + "\n" +
			   "Strongest thread profit: " + highest.getProfit() + "\n" +
			   "Strongest thread loss: " + highest.getLoss() + "\n" +
			   "Strongest thread net: " + highest.getNet() + "\n"; 
//			   acceptedLossStatus.toString();
	}
	
	@Field("GroupName")
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


	@Field("GroupUSD")
	public BigDecimal getUsd() {
		return usd;
	}


	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}


	@Field("GroupLTC")
	public BigDecimal getLtc() {
		return ltc;
	}


	public void setLtc(BigDecimal ltc) {
		this.ltc = ltc;
	}

	
	public ArrayList<Carrot> useCarrotCache() {
		//if(carrotCache != null)
		if (carrotCache.size() > getCarrotCacheNum()) {
			carrotCache.remove(0);
		}
		return carrotCache;
	}

	public ArrayList<Carrot> getCarrotCache(){

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

	@Field("GroupProfit")
	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getRunningState() {
		return runningState;
	}

	public void setRunningState(String runningState) {
		this.runningState = runningState;
	}

	@Field("GroupLoss")
	public BigDecimal getLoss() {
		return loss;
	}

	public void setLoss(BigDecimal loss) {
		this.loss = loss;
	}

	@Field("GroupNet")
	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	@Field("GroupStep")
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Field("GroupStepTotal")
	public BigDecimal getStepTotal() {
		return stepTotal;
	}

	public void setStepTotal(BigDecimal stepTotal) {
		this.stepTotal = stepTotal;
	}

	@Field("GroupStepMode")
	public String getStepMode() {
		return stepMode;
	}

	public void setStepMode(String stepMode) {
		this.stepMode = stepMode;
	}

	@Field("GroupMaxStep")
	public int getMaxStep() {
		return maxStep;
	}

	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}

//	if (t.getLifeTimeState().equals("IDLE")) {
//		idleThreadCount++;
//	} else {
//		activeThreadCount++;
//	}
//	if (t.getBuyProcessState().equals("DESIRED_BUY")) {
//		buyingThreadCount++;
//	} else if(t.getBuyProcessState().equals("BOUGHT") || t.getBuyProcessState().equals("DESIRED_SELL")) {
//		sellingThreadCount++;
//	}
//	if (t.getLifeTimeState().equals("BUY_STUCK")) {
//		buyStuckCount++;
//	} else if(t.getLifeTimeState().equals("SELL_STUCK")) {
//		sellStuckCount++;
//	}
	@Transient
	public int getIdleThreadCount() {
		idleThreadCount = 0;
		for (TradeThread t: trades) {
			if (t.getLifeTimeState().equals("IDLE")) {
				idleThreadCount++;
			}
		}
		return idleThreadCount;
	}

	@Transient
	public int getActiveThreadCount() {
	    activeThreadCount = 0;
		for (TradeThread t: trades) {
			if (!t.getLifeTimeState().equals("IDLE")) {
				activeThreadCount++;
			}
		}
		return activeThreadCount;
	}

	@Transient
	public int getBuyingThreadCount() {
		buyingThreadCount = 0;
		for (TradeThread t: trades) {
		if (t.getBuyProcessState().equals("DESIRED_BUY")) {
			buyingThreadCount++;
		}
		}
		return buyingThreadCount;
	}

	@Transient
	public int getSellingThreadCount() {
		sellingThreadCount = 0;
		for (TradeThread t: trades) {
			 if(t.getBuyProcessState().equals("BOUGHT") || t.getBuyProcessState().equals("DESIRED_SELL")) {
					sellingThreadCount++;
				}
		}
		return sellingThreadCount;
	}

	@Transient
	public int getBuyStuckCount() {
		buyStuckCount = 0;
		for (TradeThread t: trades) {
		if (t.getLifeTimeState().equals("BUY_STUCK")) {
			buyStuckCount++;
		}
		}
		return buyStuckCount;
	}

	@Transient
	public int getSellStuckCount() {
		sellStuckCount = 0;
		for (TradeThread t: trades) {
		 if(t.getLifeTimeState().equals("SELL_STUCK")) {
				sellStuckCount++;
			}
		}
		return sellStuckCount;
	}

	@Field("GroupSimMode")
	public SimulationMode getSimMode() {
		return simMode;
	}

	public void setSimMode(SimulationMode simMode) {
		this.simMode = simMode;
	}

	@Field("GroupDumpingMode")
	public String getDumpingMode() {
		return dumpingMode;
	}

	public void setDumpingMode(String dumpingMode) {
		this.dumpingMode = dumpingMode;
	}

	@Field("GroupLossMode")
	public String getLossMode() {
		return lossMode;
	}

	public void setLossMode(String lossMode) {
		this.lossMode = lossMode;
	}

	@Field("GroupSplitMode")
	public String getSplitMode() {
		return splitMode;
	}

	public void setSplitMode(String splitMode) {
		this.splitMode = splitMode;
	}

	@Field("GroupSplitNum")
	public int getSplitNum() {
		return splitNum;
	}

	public void setSplitNum(int splitNum) {
		this.splitNum = splitNum;
	}

	@Field("GroupFLT")
	public long getForceLossTimeout() {
		return forceLossTimeout;
	}

	public void setForceLossTimeout(long forceLossTimeout) {
		this.forceLossTimeout = forceLossTimeout;
	}

	@Field("GroupAccountSnapshot")
	public BigDecimal getAccountSnapshot() {
		return accountSnapshot;
	}

	public void setAccountSnapshot(BigDecimal accountSnapshot) {
		this.accountSnapshot = accountSnapshot;
	}

	@Field("GroupFee")
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
//	@Field("GroupTimer")
	@Transient
	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Field("GroupTick")
	public long getTick() {
		return tick;
	}

	public void setTick(long tick) {
		this.tick = tick;
	}

	public ArrayList<String> getAcceptedLossLog() {
		return acceptedLossLog;
	}

	public void setAcceptedLossLog(ArrayList<String> acceptedLossLog) {
		this.acceptedLossLog = acceptedLossLog;
	}

	@Field("GroupFLM")
	public String getForceLossMode() {
		return forceLossMode;
	}

	public void setForceLossMode(String forceLossMode) {
		this.forceLossMode = forceLossMode;
	}

	@Field("GroupSlightAmount")
	public BigDecimal getSlightAmount() {
		return slightAmount;
	}

	public void setSlightAmount(BigDecimal slightAmount) {
		this.slightAmount = slightAmount;
	}
}
