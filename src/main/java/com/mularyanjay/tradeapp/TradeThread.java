//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

//@Document
@AccessType(Type.PROPERTY)
public class TradeThread {

//	@Autowired
//	@Transient
//	VitalityInstance vi;
//	@Autowired
//	@Transient
//	VitalityInstanceRepository vir;
	//simMode
	@Transient
	private HttpEntityBean httpEntityBean;
	private boolean dirty;
	private BigDecimal usd;
	private BigDecimal ltc;
	private BigDecimal lastLtc;
	//buyprocessstate
	private String buyProcessState; //STANDBY, DESIRED_BUY, BOUGHT, DESIRED_SELL, SOLD, SUSPEND
	private String lifeTimeState; //IDLE, TRADING, BUY_STUCK, SELL_STUCK, RESERVE;
	private long desiredBuyTimeout; //Depends on tradegroup, change to BUY_STUCK
	private long desiredSellToStuckTimeout; //Depends on tradegroup, change to SELL_STUCK
	//Need to implement a timer (Without thread.sleep???? -_- meh)
	private BigDecimal requestBuyPrice;
	private BigDecimal requestSellPrice;
	private BigDecimal requestedTotal;
	private BigDecimal requestedLtc;
	private BigDecimal currentPrice;
	private BigDecimal profit;
	private BigDecimal profitPercentage;
	private BigDecimal lastUsd;
	@Transient
	private Timer timer;
	private SimulationMode simMode; //SIMULATION, REALTIME
	private ArrayList<Dock> docks;
	private BigDecimal stepTotal;
	private String stepMode; //NONE, STEPSHED
	private BigDecimal loss;
	private BigDecimal net;
	private String stepStatus; //CHARGING, MAXED
	private long secondTick;
	private long lastSecondTick;
	private Carrot simCarrot;
	private BigDecimal forceSellFee;
	private int splitDepth;
	private BigDecimal slightAmount;
	//private boolean traded
	//private BigDecimal currentPrice;
	//flagged bool?
	//no, do periodic counts in tradegroup to find stuck status
	//change amount of money now....
	public TradeThread() {
		
	}
	
	public TradeThread(SimulationMode sm, BigDecimal initialUSD, long whatDBT, long whatDSTST, BigDecimal slightAmount) {
	
		httpEntityBean = new HttpEntityBean();
		//setSimMode(sm);
		setDirty(false);
		setSlightAmount(slightAmount);
		setSimMode(sm);
		setForceSellFee(new BigDecimal("0.003"));
		setStepTotal(new BigDecimal("0"));
		setLoss(new BigDecimal("0"));
		setNet(new BigDecimal("0"));
		setLastSecondTick(-1L);
		setSecondTick(0L);
		setStepStatus("CHARGING");
		setUsd(initialUSD);
		setLastUsd(initialUSD);
		setLtc(new BigDecimal("0"));

		setProfit(new BigDecimal("0"));
		setBuyProcessState("STANDBY");
		setLifeTimeState("IDLE");
		setDesiredBuyTimeout(whatDBT);
		setDesiredSellToStuckTimeout(whatDSTST);
		setRequestedLtc(new BigDecimal("0"));
		setCurrentPrice(new BigDecimal("0"));
		setRequestSellPrice(new BigDecimal("0"));
		setRequestedTotal(new BigDecimal("0"));
		setTimer(new Timer());
		docks = new ArrayList<Dock>();
		docks.add(new Dock("INCOMING"));
		docks.add(new Dock("TOSTEPSHED"));
		if (getSimMode() == SimulationMode.REALTIME) {
			setTimer(new Timer());
			getTimer().schedule(new TimerTask() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					incrementSecondTick(1L);
				}
				
			}, 1000L, 1000L);
			
			}
	}
	
	public void doRestTemplate(String url, String json) {
		ResponseEntity<String> res = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			res = restTemplate.exchange(url, HttpMethod.POST, httpEntityBean.postEntityFromUrl(url, json), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
	} catch (Throwable t) {
//		e.printStackTrace();
		//System.out.println(e.getResponseBodyAsString());
		t.printStackTrace();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doRestTemplate(url, json);
	} finally {
		if (res != null) {
			System.out.println(res.getBody());
		} else {
			System.out.print("RESPONSE IS NULL");
			
		}
	}
	}
	
	public void forceLoss() {
		if (getBuyProcessState().equals("DESIRED_SELL") || getBuyProcessState().equals("BOUGHT")) {
		BigDecimal sellPrice = new BigDecimal("0");
		sellPrice = getCurrentPrice().add(getSlightAmount());//.subtract(getCurrentPrice().multiply(getForceSellFee()));
		BigDecimal forceLtc = new BigDecimal("0");
		if (getBuyProcessState().equals("DESIRED_SELL")) {
		forceLtc = getLastLtc();//getRequestedTotal().divide(getRequestSellPrice(), 8, RoundingMode.HALF_DOWN);
		} else if (getBuyProcessState().equals("BOUGHT")) {
			forceLtc = getLtc();
		} 
		// setRequestSellPrice(sellPrice);
		if (getSimMode() == SimulationMode.REALTIME) {
			ObjectMapper objectMapper = new ObjectMapper();
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Order order = new Order();
			order.setType("limit");
			order.setSide("sell");
			order.setProduct_id("ZRX-USD");
			// order.setStp("co");
			 order.setTime_in_force("GTT");
			 order.setCancel_after("hour");
//			order.setPrice(getRequestBuyPrice().toPlainString());
			order.setSize((new BigDecimal(forceLtc.toPlainString()).setScale(5, RoundingMode.HALF_DOWN)).toPlainString());
			order.setPrice((new BigDecimal(sellPrice.toPlainString()).setScale(6, RoundingMode.HALF_DOWN)).toPlainString());
//			long minutes = 0;
//			long hours = 0;
//			long days = 0;
//			if (getDesiredBuyTimeout() >= 1000L * 60L) {
//				minutes = getDesiredBuyTimeout() / (1000L * 60L);
//			}
//			if (getDesiredBuyTimeout() >= 1000L * 60L * 60L) {
//				hours = getDesiredBuyTimeout() / (1000L * 60L * 60L);
//			}
//			if (getDesiredBuyTimeout() >= (1000L * 60L * 60L * 24L)) {
//				days = getDesiredBuyTimeout() / (1000L * 60L * 60L * 24L);
//			}
//			order.setCancel_after("" + minutes + "," + hours + "," + days);
			
			String json = null;
			try {
				json = new String(objectMapper.writeValueAsString(order));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String url = "https://sample-tradeapp.herokuapp.com/placeOrder";
			doRestTemplate(url, json);
//			ResponseEntity<Order> response = 
		
					//			try {
//				System.out.println(objectMapper.writeValueAsString(response.getBody()));
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (HttpStatusCodeException e) {
//				e.printStackTrace();
//			}
		}

		//**************
		// BigDecimal forceTotal = new BigDecimal("0");
		// forceTotal = sellPrice.multiply(forceLtc);
		
		// if (forceTotal.compareTo(getLastUsd()) >= 0) {
		// 	setUsd(forceTotal);
		// 	setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		// 	setBuyProcessState("SOLD");
		// 	setLifeTimeState("RESERVE");
		// 	setLtc(new BigDecimal("0"));
		// 	setLastUsd(forceTotal);
		// } else if (forceTotal.compareTo(getLastUsd()) == -1) {
		// 	setUsd(forceTotal);
		// 	setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
		// 	setBuyProcessState("SOLD");//idle
		// 	setLifeTimeState("IDLE");
		// 	setLtc(new BigDecimal("0"));
		// 	setLastUsd(forceTotal);
		// }
		//****************


//		if (getBuyProcessState().equals("DESIRED_SELL")) {
//			if (getRequestedTotal().compareTo(forceTotal) == 1) {
//			setLoss(getLoss().add(getRequestedTotal().subtract(forceTotal)));
//			setBuyProcessState("SOLD");//idle
//			setLifeTimeState("IDLE");
//			} else {
//				if (getUsd().compareTo(getLastUsd()) == 1) {
//				setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
//				setBuyProcessState("SOLD");
//				setLifeTimeState("RESERVE");
//				} else {
//					setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
//					setBuyProcessState("SOLD");//idle
//					setLifeTimeState("IDLE");
//				}
//			}
//			} else if (getBuyProcessState().equals("BOUGHT")) {
//				if (getRequestBuyPrice().compareTo(getCurrentPrice()) == 1) {
//					setLoss(getLoss().add((getRequestBuyPrice().multiply(forceLtc))).subtract(forceTotal));
//					setBuyProcessState("SOLD");//idle
//					setLifeTimeState("IDLE");
//					} else {
//						if (getUsd().compareTo(getLastUsd()) == 1) {
//						setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
//						setBuyProcessState("SOLD");
//						setLifeTimeState("RESERVE");
//						} else {
//							setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
//							setBuyProcessState("SOLD");//idle
//							setLifeTimeState("IDLE");	
//						}
//					}
//				}
			
//			if (getSimMode() == SimulationMode.REALTIME) {
////				timer.cancel();
//				} else if (getSimMode() == SimulationMode.SIMULATION) {
					
				// 	setLtc(new BigDecimal("0"));
				// //set Litecoin
				// setBuyProcessState("DESIRED_SELL");
				// setLifeTimeState("TRADING");
				// System.out.println("Sell order placed at $" + getRequestSellPrice());
				setRequestedTotal(sellPrice.multiply(forceLtc));
				setRequestSellPrice(sellPrice);
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());

				resetTick();
					setDirty(true);
//				}
		}
	}
	
	public void forceSell() {
		if (getBuyProcessState().equals("DESIRED_SELL") || getBuyProcessState().equals("BOUGHT")) {
			
		BigDecimal sellPrice = new BigDecimal("0");
		sellPrice = getCurrentPrice().subtract(getCurrentPrice().multiply(getForceSellFee()));
		BigDecimal forceLtc = new BigDecimal("0");
		if (getBuyProcessState().equals("DESIRED_SELL")) {
		forceLtc = getRequestedTotal().divide(getRequestSellPrice(), 8, RoundingMode.HALF_DOWN);
		} else if (getBuyProcessState().equals("BOUGHT")) {
			forceLtc = getLtc();
		}
		BigDecimal forceTotal = new BigDecimal("0");
		forceTotal = sellPrice.multiply(forceLtc);
		if (forceTotal.compareTo(getLastUsd()) == 1) {
			setUsd(forceTotal);
			setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
			setBuyProcessState("SOLD");
			setLifeTimeState("RESERVE");
			setLtc(new BigDecimal("0"));
			setLastUsd(forceTotal);
			if (getSimMode() == SimulationMode.REALTIME) {
				timer.cancel();
				} else if (getSimMode() == SimulationMode.SIMULATION) {
					resetTick();
				}
		}
//		setUsd(forceTotal);
//		if (getUsd().compareTo(getLastUsd()) == 1) {
//			
//		if (getBuyProcessState().equals("DESIRED_SELL")) {
//		if (getRequestedTotal().compareTo(forceTotal) == 1) {
//		setLoss(getLoss().add(getRequestedTotal().subtract(forceTotal)));
//		setBuyProcessState("SOLD");//idle
//		setLifeTimeState("IDLE");
//		} else {
//			if (getUsd().compareTo(getLastUsd()) == 1) {
//			setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
//			setBuyProcessState("SOLD");
//			setLifeTimeState("RESERVE");
//			} else {
//				setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
//				setBuyProcessState("SOLD");//idle
//				setLifeTimeState("IDLE");
//			}
//		}
//		} else if (getBuyProcessState().equals("BOUGHT")) {
//			if (getRequestBuyPrice().compareTo(getCurrentPrice()) == 1) {
//				setLoss(getLoss().add((getRequestBuyPrice().multiply(forceLtc))).subtract(forceTotal));
//				setBuyProcessState("SOLD");//idle
//				setLifeTimeState("IDLE");
//				} else {
//					if (getUsd().compareTo(getLastUsd()) == 1) {
//					setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
//					setBuyProcessState("SOLD");
//					setLifeTimeState("RESERVE");
//					} else {
//						setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
//						setBuyProcessState("SOLD");//idle
//						setLifeTimeState("IDLE");	
//					}
//				}
//			}
//		
		
//		setLtc(new BigDecimal("0"));
//		setLastUsd(forceTotal);
//		}
//		vir.save(vi);
		setDirty(true);
		}
	}
	
	public void cancelBuy() {
		if(getLifeTimeState().equals("BUY_STUCK") || getBuyProcessState().equals("SUSPEND")) {
			setUsd(getLastUsd());
			setBuyProcessState("STANDBY");
			setLifeTimeState("IDLE");
			System.out.println("Buy order canceled");
		}
	}
	
	public void calculateNet() {
		setNet(getProfit().subtract(getLoss()));
	}
	public void evaluateSimulationTimeout() {
		if (getLastSecondTick() > -1L) {
		if (getBuyProcessState().equals("DESIRED_BUY")) {
			if (getSecondTick() - getLastSecondTick() > getDesiredBuyTimeout()/1000) {
				setLifeTimeState("BUY_STUCK");
				cancelBuy();
				setDirty(true);
			}
		} else if (getBuyProcessState().equals("BOUGHT") || getBuyProcessState().equals("DESIRED_SELL")) {
			if (getSecondTick() - getLastSecondTick() > getDesiredSellToStuckTimeout()/1000) {
				setLifeTimeState("SELL_STUCK");
				setDirty(true);
			}
		}
		}
	}
	
	public void incrementSecondTick() {
		setSecondTick(getSecondTick() + 1L);
		evaluateSimulationTimeout();
	}
	
	public void incrementSecondTick(long amount) {
		setSecondTick(getSecondTick() + amount);
		evaluateSimulationTimeout();
	}
	
	public void checkMaxed() {
		if (getUsd().compareTo(getStepTotal()) >= 0) {
			setStepStatus("MAXED");
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
	
	public void shedAllFromIncoming() {
		BigDecimal incomingTotal = new BigDecimal(getDockByName("INCOMING").getUsd().toString());
		setUsd(getUsd().add(incomingTotal));
		getDockByName("INCOMING").setUsd(new BigDecimal("0"));
	}
	
	public void shedExcessToShedStep() {
		BigDecimal amountToSubtract = new BigDecimal("0");
		if (getUsd().compareTo(getStepTotal()) == 1) {
			amountToSubtract = getUsd().subtract(getStepTotal());
		}
		setUsd(getUsd().subtract(amountToSubtract));
		setProfit(getProfit().subtract(amountToSubtract));
		getDockByName("TOSTEPSHED").addUsd(amountToSubtract);
	}
	
	public void resetTick() {
		if(getBuyProcessState().equals("DESIRED_BUY")){// || getBuyProcessState().equals("BOUGHT")) {
			setLastSecondTick(getSecondTick());
		} else {
			setLastSecondTick(-1L);
		}
	}
	//set desired buy and what not.  Timer/timeouts for if it gets stuck
	//Remember to set timer/timeouts
	public void deploy(Carrot carrot) {
		if (getSimMode() != null && getCurrentPrice() != null &&carrot != null && carrot.getLow() != null) {
		if ((getSimMode() == SimulationMode.SIMULATION) || (getCurrentPrice().compareTo(carrot.getLow().subtract(getSlightAmount())) == 1)) {
		//Have to make sure things are set up correctly
			
		//At this point I would place buy order with api	
		setRequestBuyPrice(carrot.getLow().subtract(getSlightAmount()));
		//placeBuyOrder();
		//Need to calculate totals and then do transaction
		//A buy order will deduct dollars and want ltc,
		//but will possess no ltc until it is met.
		setRequestedLtc(getUsd().divide(getRequestBuyPrice(), 8, RoundingMode.HALF_DOWN));
		if (getSimMode() == SimulationMode.REALTIME) {
			ObjectMapper objectMapper = new ObjectMapper();
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Order order = new Order();
			order.setType("limit");
			order.setSide("buy");
			order.setProduct_id("ZRX-USD");
			order.setStp("co");
			// order.setPost_only("true");
			
			
//			BigDecimal temp = new BigDecimal(getRequestBuyPrice().toPlainString());

			order.setPrice((new BigDecimal(getRequestBuyPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)).toPlainString());
			order.setSize((new BigDecimal(getRequestedLtc().toPlainString()).setScale(5, RoundingMode.HALF_DOWN)).toPlainString());
			long minutes = 0;
			long hours = 0;
			long days = 0;
			if (getDesiredBuyTimeout() >= 1000L * 60L) {
				minutes = getDesiredBuyTimeout() / (1000L * 60L);
			}
			if (getDesiredBuyTimeout() >= 1000L * 60L * 60L) {
				hours = getDesiredBuyTimeout() / (1000L * 60L * 60L);
			}
			if (getDesiredBuyTimeout() >= (1000L * 60L * 60L * 24L)) {
				days = getDesiredBuyTimeout() / (1000L * 60L * 60L * 24L);
			}
			order.setTime_in_force("GTT");
			order.setCancel_after("hour");
			
			String json = null;
			try {
				json = new String(objectMapper.writeValueAsString(order));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (json != null) {
			System.out.println(json);
			} else {
				System.out.println("JSON is Null!!!");
			}
			String url = "https://sample-tradeapp.herokuapp.com/placeOrder";
			doRestTemplate(url, json);
//			RestTemplate restTemplate = new RestTemplate();
//			String url = "https://api.gdax.com/orders";
////			 response;
////			try {
////				ResponseEntity response = 
//				restTemplate.exchange(url, HttpMethod.POST, httpEntityBean.postEntityFromUrl(url, "'" + json + "'"), new ParameterizedTypeReference<Order>(){});//restTemplate.exchange(requestEntity, responseType)//
//				System.out.println(objectMapper.writeValueAsString(response.getBody()));
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (HttpStatusCodeException e) {
//				e.printStackTrace();
//			}
		}
		setLastUsd(getUsd());
		setUsd(getUsd().subtract(getRequestBuyPrice().multiply(getRequestedLtc())));
		setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		//Sysout?
		System.out.println("Buy order placed at $" + getRequestBuyPrice());
//		vir.save(vi);
		setDirty(true);
		//Make timer change to stuck if not at different
		//buy process state, otherwise change to trading
		//Make getCurrentTime for Carrot?
		//Change type of timeouts to long
		
//		if (getSimMode() == SimulationMode.REALTIME) {
//		setTimer(new Timer());
//		getTimer().schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (getBuyProcessState().equals("DESIRED_BUY")) {
//					setLifeTimeState("BUY_STUCK");
//					cancelBuy();
////					vir.save(vi);
//					setDirty(true);
//				}
//				timer.cancel();
//			}
//			
//		}, getDesiredBuyTimeout());
//		
//		} else if (getSimMode() == SimulationMode.SIMULATION) {
//			//setLastSecondTick(getSecondTick());
//			//,,,
			resetTick();
//		}
		}
		
		} else {
			if (getSimMode() == null) {
				System.out.println("sim mode null");
			}
			if (getCurrentPrice() == null) {
				System.out.println("current price null");
			}
			if (carrot == null) {
				System.out.println("carrot null");
			}
			if (carrot.getLow() == null) {
				System.out.println("carrot low null");
			}
		}
	}
	
	//Make sure carrot sell price is more than buy price
	public void attemptSell(Carrot carrot) {
		if (getSimMode() != null && getCurrentPrice() != null &&carrot != null && carrot.getHigh() != null) {
			
		if ((getSimMode() == SimulationMode.SIMULATION) || (getCurrentPrice().compareTo(carrot.getHigh().add(getSlightAmount())) == -1)) {
			setRequestSellPrice(carrot.getHigh().add(getSlightAmount()));
			if (getRequestSellPrice().compareTo(getRequestBuyPrice()) == 1) {
			
				setRequestedTotal(getRequestSellPrice().multiply(getLtc()));
				if (getSimMode() == SimulationMode.REALTIME) {
					ObjectMapper objectMapper = new ObjectMapper();
//					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					Order order = new Order();
					order.setType("limit");
					order.setSide("sell");
					order.setProduct_id("ZRX-USD");
					order.setStp("co");
//					order.setPost_only("true");
					order.setPrice((new BigDecimal(getRequestSellPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)).toPlainString());
					order.setSize((new BigDecimal(getLtc().toPlainString()).setScale(5, RoundingMode.HALF_DOWN)).toPlainString());
					long minutes = 0;
					long hours = 0;
					long days = 0;
					if (getDesiredSellToStuckTimeout() >= 1000L * 60L) {
						minutes = getDesiredSellToStuckTimeout() / (1000L * 60L);
					}
					if (getDesiredSellToStuckTimeout() >= 1000L * 60L * 60L) {
						hours = getDesiredSellToStuckTimeout() / (1000L * 60L * 60L);
					}
					if (getDesiredSellToStuckTimeout() >= (1000L * 60L * 60L * 24L)) {
						days = getDesiredSellToStuckTimeout() / (1000L * 60L * 60L * 24L);
					}
					order.setTime_in_force("GTT");
					order.setCancel_after("hour");
//					
					String json = null;
					try {
						json = new String(objectMapper.writeValueAsString(order));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url = "https://sample-tradeapp.herokuapp.com/placeOrder";
					doRestTemplate(url, json);
//					RestTemplate restTemplate = new RestTemplate();
//					String url = "https://api.gdax.com/orders";
////					ResponseEntity<Order> response;
//					restTemplate.exchange(url, HttpMethod.POST, httpEntityBean.postEntityFromUrl(url, "'" + json + "'"), new ParameterizedTypeReference<Order>(){});//restTemplate.exchange(requestEntity, responseType)//
//					try {
////						System.out.println(objectMapper.writeValueAsString(response.getBody()));
//					} catch (JsonProcessingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				setLastLtc(getLtc());
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());
//				vir.save(vi);
				setDirty(true);
	
				}
			}
		} else {
			if (getSimMode() == null) {
				System.out.println("sim mode null");
			}
			if (getCurrentPrice() == null) {
				System.out.println("current price null");
			}
			if (carrot == null) {
				System.out.println("carrot null");
			}
			if (carrot.getHigh() == null) {
				System.out.println("carrot high null");
			}
		}
		
	}
	
	public void broadcastCarrot(Carrot carrot) {
		setCurrentPrice(carrot.getCurrent());
		setSimCarrot(carrot);
		refresh();
	}
	
	
	public void refresh() {
		if(getBuyProcessState().equals("DESIRED_BUY")) {
			//if current price lower then desired buy then 
			//processBuy(which is buy()), store requested ltc
			//into ltc
			if (getSimMode() == SimulationMode.REALTIME) {
			if(getCurrentPrice().compareTo(getRequestBuyPrice()) == -1) {
				buy();
//				vir.save(vi);
				setDirty(true);
			}
			} else if(getSimMode() == SimulationMode.SIMULATION) {
				if (getRequestBuyPrice().compareTo(getSimCarrot().getHigh()) == -1) {
					buy();
				}
			}
		} else if (getBuyProcessState().equals("DESIRED_SELL")) {
			if (getSimMode() == SimulationMode.REALTIME) {
			if(getCurrentPrice().compareTo(getRequestSellPrice()) == 1) {
				sell();
//				vir.save(vi);
				setDirty(true);
			}
			} else if(getSimMode() == SimulationMode.SIMULATION) {
				if (getRequestSellPrice().compareTo(getSimCarrot().getLow()) == 1) {
					sell();
				}
			}
		}
	}
	
	//Ok, now to do sells (start in tradegroup)
	public void buy() {
		setLtc(getRequestedLtc());
		setBuyProcessState("BOUGHT");
		setLifeTimeState("TRADING");
		System.out.println("Bought at $" + getRequestBuyPrice());
//		if (getSimMode() == SimulationMode.REALTIME) {
//		timer.cancel();
//		} else if (getSimMode() == SimulationMode.SIMULATION) {
			resetTick();
//		}
//		if(getSimMode() == SimulationMode.REALTIME) {
//			setTimer(new Timer());
//			getTimer().schedule(new TimerTask() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if (getBuyProcessState().equals("BOUGHT") || getBuyProcessState().equals("DESIRED_SELL")) { //"DESIRED_SELL"
//						setLifeTimeState("SELL_STUCK");
////						vir.save(vi);
//						setDirty(true);					}
//					timer.cancel();
//				}
//				
//			}, getDesiredSellToStuckTimeout());
//			} else if (getSimMode() == SimulationMode.SIMULATION) {
//				//setLastSecondTick(getSecondTick());
//				resetTick();
//			}
	}
	
	public void sell() {
		
		// if (forceTotal.compareTo(getLastUsd()) >= 0) {
		// 	setUsd(forceTotal);
		// 	setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		// 	setBuyProcessState("SOLD");
		// 	setLifeTimeState("RESERVE");
		// 	setLtc(new BigDecimal("0"));
		// 	setLastUsd(forceTotal);
		// } else if (forceTotal.compareTo(getLastUsd()) == -1) {
		// 	setUsd(forceTotal);
		// 	setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
		// 	setBuyProcessState("SOLD");//idle
		// 	setLifeTimeState("IDLE");
		// 	setLtc(new BigDecimal("0"));
		// 	setLastUsd(forceTotal);
		// }
		setUsd(getRequestedTotal());
		if ((getUsd().subtract(getLastUsd())).compareTo(getSlightAmount()) >= 0){
		setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		setLastUsd(getUsd());
		//profit percentage?
		setBuyProcessState("SOLD");
		setLifeTimeState("RESERVE");
		} 
		else {
			setLoss(getLoss().add((getLastUsd()).subtract(getUsd())));
			setLastUsd(getUsd());
		//profit percentage?
		setBuyProcessState("SOLD");
		setLifeTimeState("IDLE");
		}
		setLastLtc(new BigDecimal("0"));
		System.out.println("Sold at $" + getRequestSellPrice());
//		if (getSimMode() == SimulationMode.REALTIME) {
//			timer.cancel();
//			} else if (getSimMode() == SimulationMode.SIMULATION) {
				resetTick();
//			}
	}
	//request... blah?

	@Field("ThreadUSD")
	public BigDecimal getUsd() {
		return usd;
	}

	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}

	@Field("ThreadLtc")
	public BigDecimal getLtc() {
		return ltc;
	}

	public void setLtc(BigDecimal ltc) {
		this.ltc = ltc;
	}

	@Field("ThreadBPS")
	public String getBuyProcessState() {
		return buyProcessState;
	}

	public void setBuyProcessState(String buyProcessState) {
		this.buyProcessState = buyProcessState;
	}

	@Field("ThreadLTS")
	public String getLifeTimeState() {
		return lifeTimeState;
	}

	public void setLifeTimeState(String lifeTimeState) {
		this.lifeTimeState = lifeTimeState;
	}

	@Field("ThreadDBT")
	public long getDesiredBuyTimeout() {
		return desiredBuyTimeout;
	}

	public void setDesiredBuyTimeout(long desiredBuyTimeout) {
		this.desiredBuyTimeout = desiredBuyTimeout;
	}

	@Field("ThreadDSTST")
	public long getDesiredSellToStuckTimeout() {
		return desiredSellToStuckTimeout;
	}

	public void setDesiredSellToStuckTimeout(long desiredSellToStuckTimeout) {
		this.desiredSellToStuckTimeout = desiredSellToStuckTimeout;
	}

	@Field("ThreadRBP")
	public BigDecimal getRequestBuyPrice() {
		return requestBuyPrice;
	}

	public void setRequestBuyPrice(BigDecimal requestBuyPrice) {
		this.requestBuyPrice = requestBuyPrice;
	}

	@Field("ThreadRSP")
	public BigDecimal getRequestSellPrice() {
		return requestSellPrice;
	}

	public void setRequestSellPrice(BigDecimal requestSellPrice) {
		this.requestSellPrice = requestSellPrice;
	}
	
	@Field("ThreadRLTC")
	public BigDecimal getRequestedLtc() {
		return requestedLtc;
	}

	public void setRequestedLtc(BigDecimal requestedLtc) {
		this.requestedLtc = requestedLtc;
	}

	@Field("ThreadRTotal")
	public BigDecimal getRequestedTotal() {
		return requestedTotal;
	}

	public void setRequestedTotal(BigDecimal requestedTotal) {
		this.requestedTotal = requestedTotal;
	}

	@Field("ThreadCurrentPrice")
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	@Field("ThreadProfit")
	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	@Field("ThreadProfitPercentage")
	public BigDecimal getProfitPercentage() {
		return profitPercentage;
	}

	public void setProfitPercentage(BigDecimal profitPercentage) {
		this.profitPercentage = profitPercentage;
	}

	@Field("ThreadLastUSD")
	public BigDecimal getLastUsd() {
		return lastUsd;
	}

	public void setLastUsd(BigDecimal lastUsd) {
		this.lastUsd = lastUsd;
	}

	@Transient
	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Field("ThreadStepTotal")
	public BigDecimal getStepTotal() {
		return stepTotal;
	}

	public void setStepTotal(BigDecimal stepTotal) {
		this.stepTotal = stepTotal;
	}

	@Field("ThreadStepMode")
	public String getStepMode() {
		return stepMode;
	}

	public void setStepMode(String stepMode) {
		this.stepMode = stepMode;
	}

	@Field("ThreadLoss")
	public BigDecimal getLoss() {
		return loss;
	}

	public void setLoss(BigDecimal loss) {
		this.loss = loss;
	}

	@Field("ThreadNet")
	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	@Field("ThreadStepStatus")
	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	@Field("ThreadSecondTick")
	public long getSecondTick() {
		return secondTick;
	}

	public void setSecondTick(long secondTick) {
		this.secondTick = secondTick;
	}

	@Field("ThreadLastSecondTick")
	public long getLastSecondTick() {
		return lastSecondTick;
	}

	public void setLastSecondTick(long lastSecondTick) {
		this.lastSecondTick = lastSecondTick;
	}

	@Field("ThreadSimCarrot")
	public Carrot getSimCarrot() {
		return simCarrot;
	}

	public void setSimCarrot(Carrot simCarrot) {
		this.simCarrot = simCarrot;
	}

	@Field("ThreadSimMode")
	public SimulationMode getSimMode() {
		return simMode;
	}

	public void setSimMode(SimulationMode simMode) {
		this.simMode = simMode;
	}

	@Field("ThreadForceSellFee")
	public BigDecimal getForceSellFee() {
		return forceSellFee;
	}

	public void setForceSellFee(BigDecimal forceSellFee) {
		this.forceSellFee = forceSellFee;
	}

	@Field("ThreadSplitDepth")
	public int getSplitDepth() {
		return splitDepth;
	}

	public void setSplitDepth(int splitDepth) {
		this.splitDepth = splitDepth;
	}
	
	@Field("ThreadSlightAmount")
	public BigDecimal getSlightAmount() {
		return slightAmount;
	}

	public void setSlightAmount(BigDecimal slightAmount) {
		this.slightAmount = slightAmount;
	}
	
	@Field("ThreadDirty")
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public BigDecimal getLastLtc(){
		return lastLtc;
	}

	public void setLastLtc(BigDecimal lastLtc){
		this.lastLtc = lastLtc;
	}
}
