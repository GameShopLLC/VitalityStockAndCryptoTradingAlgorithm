//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.*;

// import java.util.ArrayList;
// import java.util.Timer;
// import java.util.TimerTask;
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
import com.fasterxml.jackson.databind.*;
// import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import java.util.*;
// import com.fasterxml.jackson.databind.DeserializationFeature;
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
	private String orderId;
	private IncomingOrder activeOrder;
	private BigDecimal lastPartialFill;
	private String partialState;// NONE, PARTIAL
	// @Transient
	private HttpEntityBean httpEntityBean;
	private HttpEntityBean localHttpEntityBean;
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
		localHttpEntityBean = new HttpEntityBean();
		//setSimMode(sm);
		setActiveOrder(new IncomingOrder());
		setOrderId(new String(""));
		setDirty(false);
		setSlightAmount(slightAmount);
		setSimMode(sm);
		setForceSellFee(new BigDecimal("0.003"));
		setStepTotal(new BigDecimal("0"));
		setLoss(new BigDecimal("0"));
		setNet(new BigDecimal("0"));
		setLastSecondTick(-1L);
		setSecondTick(0L);
		setPartialState("NONE");
		setStepStatus("CHARGING");
		setUsd(initialUSD);
		setLastUsd(initialUSD);
		setLtc(new BigDecimal("0"));
		setLastLtc(new BigDecimal("0"));
		setLastPartialFill(new BigDecimal("0"));
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
		// docks.add(new Dock("PARTIALFILL"));
		startTimer();
	}
	public void startTimer() {
			if (getSimMode() == SimulationMode.REALTIME) {
			setTimer(new Timer());
			getTimer().schedule(new TimerTask() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					incrementSecondTick(1L);

					//  if (getBuyProcessState().equals("SOLD")){
					// 	setLifeTimeState(new String("RESERVE"));
					// }
					 // if (getActiveOrder().getId() != null) {

						
						 // fetchOrder();	
					// } 
				}
				
			}, 1000L, 1000L);
			
			}
	}

	public BigDecimal calculateSpread() {
				ObjectMapper objectMapper = new ObjectMapper();
				RestTemplate restTemplate = new RestTemplate();
				String url = "https://ancient-crag-48261.herokuapp.com/testbackendrequest";
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, localHttpEntityBean.getLocalEntityFromUrl(url,"application/json"), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
				// settData(response.getBody());
				TickerData tickerData = null;
				try {
					tickerData = objectMapper.readValue(response.getBody(), TickerData.class);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (tickerData != null){
					return new BigDecimal((tickerData.getAsk().subtract(tickerData.getBid())).toPlainString());
				}
				try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SPREAD NULL");
				return calculateSpread();
	}

	public boolean doRestTemplate(String url, String json) {
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

		return false;
	} finally {
		if (res != null) {
			if (res.getBody().toString().contains("undefined")) {
				// setOrderId(null);
				// setActiveOrder(null);
				// doRestTemplate(url, json);
				return false;
			} else {
			System.out.println("The id is:" + res.getBody());
			setOrderId(new String(res.getBody()));
			if(fetchOrder()){
				return true;
			}
			return false;
		}
		}
			System.out.print("RESPONSE IS NULL");

			
			return false;
	}
	}

	public void cancelOrder() {
		ResponseEntity<String> res = null;
		// String retval = new String("");
		try {
			RestTemplate restTemplate = new RestTemplate();
			// res = restTemplate.exchange("https://sample-tradeapp.herokuapp.com/cancelOrder/" + getOrderId(), HttpMethod.DELETE, httpEntityBean.getEntityFromUrl("https://sample-tradeapp.herokuapp.com/cancelOrder/" + getOrderId()), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
			restTemplate.delete("https://sample-tradeapp.herokuapp.com/cancelOrder/" + getOrderId());
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
		// cancelOrder();
	} finally {
		// if (res != null) {
			System.out.println("Removed Order:");// + res.getBody());
			// setOrderId(new String(res.getBody()));




			setOrderId(null);
			setActiveOrder(null);
		// } else {
		// 	System.out.print("REMOVE RESPONSE IS NULL");
			
		// }
	}
	// if (!retval.equals("")){
	// 	return retval;
	// }
	// if (res.getBody().toString().contains("message")) {
	// 	return "MESSAGE";
	// } else if (res == null) {
	// 	return "NULL";
	// }
	// return res.getBody().toString();
	}

	// 
	public boolean fetchOrder() {
			ResponseEntity<String> res = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
		// 	 List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
  //           //Add the Jackson Message converter
  //  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  //  // Note: here we are making this converter to process any kind of response, 
  //  // not only application/*json, which is the default behaviour
  // // converter.setSupportedMediaTypes(Arrays.asList({MediaType.ALL}));    
  // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));     
  //  messageConverters.add(converter);  
  //  restTemplate.setMessageConverters(messageConverters);  
			if (getOrderId() != null){
res = restTemplate.exchange("https://sample-tradeapp.herokuapp.com/getOrder/" + getOrderId(), HttpMethod.GET, httpEntityBean.getEntityFromUrl("https://sample-tradeapp.herokuapp.com/getOrder/" + getOrderId()), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
	
			} else if (getActiveOrder().getId() != null){
				res = restTemplate.exchange("https://sample-tradeapp.herokuapp.com/getOrder/" + getActiveOrder().getId(), HttpMethod.GET, httpEntityBean.getEntityFromUrl("https://sample-tradeapp.herokuapp.com/getOrder/" + getActiveOrder().getId()), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
	
			} else {
				return false;
			}
			
	} catch (Throwable t) {
//		e.printStackTrace();
		//System.out.println(e.getResponseBodyAsString());
		t.printStackTrace();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!getLifeTimeState().equals("BUY_STUCK") && !getLifeTimeState().equals("SELL_STUCK")){
			fetchOrder();
		} else {
			return false;
		}
		
	} finally {
		if (res != null) {
			System.out.println(res.getBody());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
			if (res.getBody().equals("undefined")) {
				//setOrderId(null);
				return false;
			} else {


			try {
				
			//setActiveOrder(null);
			objectMapper.readerForUpdating(activeOrder).withType(IncomingOrder.class).readValue(res.getBody().toString());
			//objectMapper.readValue(res.getBody().toString(), IncomingOrder.class);
			return true;
			} catch (Throwable throwable){
				throwable.printStackTrace();
				System.out.println("CANNOT READ ACTIVE ORDER");
			} 
			}
}
		// } else {
			System.out.print("RESPONSE IS NULL");
			return false;
			
			
		// }
	}
	}


	
	public void forceLoss() {
		//if (getBuyProcessState().equals("DESIRED_SELL") || getBuyProcessState().equals("BOUGHT")) {
		// 	setActiveOrder(null);
		// setOrderId(null);

		BigDecimal sellPrice = new BigDecimal(getCurrentPrice().add(new BigDecimal(".0001")).toPlainString());//.subtract(getCurrentPrice().multiply(getForceSellFee()));
		BigDecimal forceLtc = new BigDecimal("0");
		if (getBuyProcessState().equals("DESIRED_SELL")) {
		forceLtc = new BigDecimal(getLastLtc().toPlainString());//getRequestedTotal().divide(getRequestSellPrice(), 8, RoundingMode.HALF_DOWN);
		} else if (getBuyProcessState().equals("BOUGHT")) {
			if (getLtc().compareTo(new BigDecimal("0")) == 1) {
				forceLtc = new BigDecimal(getLtc().toPlainString());
			} else {
				forceLtc = new BigDecimal(getLastLtc().toPlainString());
			}
			
		} 

		// if (getPartialState().equals("PARTIAL")){
		// 	if(getBuyProcessState().equals("DESIRED_SELL")){
		// 		forceLtc = new BigDecimal((getLastLtc().subtract(getLastPartialFill())).toPlainString());
		// 		// forceLtc = getLastLtc();

		// 	} else if(getBuyProcessState().equals("BOUGHT")) {
		// 		forceLtc = new BigDecimal((getLtc().subtract(getLastPartialFill())).toPlainString());
		// 		// forceLtc = getLtc();
		// 	}
		// 	// forceLtc = forceLtc.subtract(getLastPartialFill());
		// } 
		// setRequestSellPrice(sellPrice);
		if (getSimMode() == SimulationMode.REALTIME) {
			timer.cancel();
			ObjectMapper objectMapper = new ObjectMapper();
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Order order = new Order();
			order.setType("limit");
			order.setSide("sell");
			order.setProduct_id("ZRX-USD");
			order.setStp("co");
			 order.setTime_in_force("GTT");
			 order.setCancel_after("hour");
//			order.setPrice(getRequestBuyPrice().toPlainString());
			 forceLtc = (new BigDecimal(forceLtc.toPlainString()).setScale(5, RoundingMode.HALF_DOWN));
			order.setSize(forceLtc.toPlainString());
			sellPrice = (new BigDecimal(sellPrice.toPlainString()).setScale(6, RoundingMode.HALF_DOWN));
			order.setPrice(sellPrice.toPlainString());
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

			if(doRestTemplate(url, json)){
				setRequestedTotal(new BigDecimal(sellPrice.multiply(new BigDecimal(forceLtc.toPlainString())).toPlainString()));
				setRequestSellPrice(new BigDecimal(sellPrice.toPlainString()));
				// setLtc(new BigDecimal("0"));
				setLastLtc(new BigDecimal(forceLtc.toPlainString()));
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + sellPrice);

				resetTick();
					setDirty(true);
					
			} else {
				System.out.println("FORCE SELL FAILED");
			}
			startTimer();
//			ResponseEntity<Order> response = 
		
					//			try {
//				System.out.println(objectMapper.writeValueAsString(response.getBody()));
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (HttpStatusCodeException e) {
//				e.printStackTrace();
//			}
		} else {
			setRequestedTotal(new BigDecimal(sellPrice.multiply(new BigDecimal(forceLtc.toPlainString())).toPlainString()));
				setRequestSellPrice(new BigDecimal(sellPrice.toPlainString()));
				// setLtc(new BigDecimal("0"));
				setLastLtc(new BigDecimal(forceLtc.toPlainString()));
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + sellPrice);

				resetTick();
					// setDirty(true);
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
				
//				}
		//}
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
			// setOrderId(null);
			// setActiveOrder(null);
		//if(getLifeTimeState().equals("BUY_STUCK")){ //|| getBuyProcessState().equals("SUSPEND")) {
			//PARTIAL STATE
			// cancelOrder();

			//if (cancel.contains(getOrderId())){
		timer.cancel();
// 			if(getCurrentPrice().compareTo(getRequestBuyPrice()) == -1) {
// 				setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				buy();
// //				vir.save(vi);
// 				setDirty(true);
// 			return;
// 			}
// 			else if (getActiveOrder() != null)	{
			
// 			if (getActiveOrder().getSettled() == true){//if(getCurrentPrice().compareTo(getRequestBuyPrice()) == -1) {
// 				setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				buy();
// //				vir.save(vi);
// 				setDirty(true);
// 			return;
// 			} 
// 		} 
		 // setOrderId(null);
		 // setActiveOrder(null);
		if (getPartialState().equals("NONE")){
			setUsd(getLastUsd());
			setBuyProcessState("STANDBY");
			setLifeTimeState("IDLE");

			System.out.println("Buy order canceled");
		} else if (getPartialState().equals("PARTIAL")){
			deployPartial();
		}
		startTimer();
		setDirty(true);
	// } else {
	// 	System.out.println("cancel failed");
	// 	System.out.println(cancel);
	// 	System.exit(0);
	// }
	//	}
	}
	
	
	public void deployPartial(){
if (getSimMode() == SimulationMode.REALTIME) {

	// setOrderId(null);
	// setActiveOrder(null);
			ObjectMapper objectMapper = new ObjectMapper();
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Order order = new Order();
			order.setType("limit");
			order.setSide("buy");
			order.setProduct_id("ZRX-USD");
			order.setStp("co");
			// order.setPost_only("true");
			
			
//			BigDecimal temp = new BigDecimal(getRequestBuyPrice().toPlainString());
			setRequestBuyPrice(new BigDecimal((new BigDecimal(getRequestBuyPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)).toPlainString()));
			setRequestedLtc(new BigDecimal((new BigDecimal(getRequestedLtc().toPlainString()).setScale(5, RoundingMode.HALF_DOWN)).toPlainString()));
			order.setPrice(getRequestBuyPrice().toPlainString());
			order.setSize(getRequestedLtc().subtract(getLastPartialFill()).toPlainString());

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
			if (doRestTemplate(url, json)) {
				if(getBuyProcessState().equals("BUY_STUCK")) {
			resetTick();
		}
				setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		//Sysout?
		System.out.println("Partial buy order placed at $" + getRequestBuyPrice());
			} else {
				System.out.println("PARTIAL BUY FAILED");
			}
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
		} else {
		if(getBuyProcessState().equals("BUY_STUCK")) {
			resetTick();
		}
				setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		//Sysout?
		System.out.println("Partial buy order placed at $" + getRequestBuyPrice());
	}
		// resetTick();
	}



	public void sellPartial() {
		if (getSimMode() == SimulationMode.REALTIME) {
					ObjectMapper objectMapper = new ObjectMapper();
//					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					Order order = new Order();
					order.setType("limit");
					order.setSide("sell");
					order.setProduct_id("ZRX-USD");
					order.setStp("co");
//					order.setPost_only("true");
					// setRequestSellPrice((new BigDecimal(getRequestSellPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)).toPlainString());
					setLastLtc(new BigDecimal((new BigDecimal(getLastLtc().subtract(getLastPartialFill()).toPlainString()).setScale(5, RoundingMode.HALF_DOWN)).toPlainString()));
					order.setPrice((getRequestSellPrice().add(new BigDecimal(".00005"))).toPlainString());
					order.setSize(getLastLtc().toPlainString());
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
					if(doRestTemplate(url, json)){
					setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());
//				vir.save(vi);
				setDirty(true);
					} else {
						System.out.println("SELL PARTIAL FAILED");
					}
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
				} else {

				
				// setLastLtc(getLtc());
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());
//				vir.save(vi);
				setDirty(true);
			}
	}

	public void calculateNet() {
		setNet(getProfit().add(getLoss()));
	}
	public void evaluateSimulationTimeout() {
		if (getLastSecondTick() > -1L) {
			if (getSimMode() == SimulationMode.SIMULATION){

			
		if (getBuyProcessState().equals("DESIRED_BUY")) {
			if (getSecondTick() - getLastSecondTick() > getDesiredBuyTimeout()/1000) {
				setLifeTimeState("BUY_STUCK");
				// setDirty(true);
			}
		} else if (getBuyProcessState().equals("BOUGHT") || getBuyProcessState().equals("DESIRED_SELL")) {
			if (getSecondTick() - getLastSecondTick() > getDesiredSellToStuckTimeout()/1000) {
				setLifeTimeState("SELL_STUCK");
				// setDirty(true);
			}
		}
	} else if (getSimMode() == SimulationMode.REALTIME){
		if (getBuyProcessState().equals("BOUGHT")) {
			if (getSecondTick() - getLastSecondTick() > getDesiredSellToStuckTimeout()/1000) {
			setLifeTimeState("SELL_STUCK");

			setDirty(true);
		}
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
		// if(getBuyProcessState().equals("DESIRED_BUY")){// || getBuyProcessState().equals("BOUGHT")) {
		// 	setLastSecondTick(getSecondTick());
		// } else {
		// 	setLastSecondTick(-1L);
		// }
		setLastSecondTick(getSecondTick());
	}
	//set desired buy and what not.  Timer/timeouts for if it gets stuck
	//Remember to set timer/timeouts
	public void deploy(Carrot carrot) {
		if (getSimMode() != null && getCurrentPrice() != null &&carrot != null && carrot.getLow() != null) {
		if ((getSimMode() == SimulationMode.SIMULATION) || (getCurrentPrice().compareTo(carrot.getLow().subtract(getSlightAmount())) == 1)) {
		//Have to make sure things are set up correctly
			
		//At this point I would place buy order with api	
			if((getSimMode() == SimulationMode.SIMULATION)){
				setRequestBuyPrice(carrot.getLow().subtract(getSlightAmount()));
			} else if ((getSimMode() == SimulationMode.REALTIME)) {
				setRequestBuyPrice(carrot.getLow().subtract(getSlightAmount().add(calculateSpread())));
			}
		
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
			setRequestBuyPrice((new BigDecimal(getRequestBuyPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)));
			setRequestedLtc((new BigDecimal(getRequestedLtc().toPlainString()).setScale(5, RoundingMode.HALF_DOWN)));
			order.setPrice(getRequestBuyPrice().toPlainString());
			order.setSize(getRequestedLtc().toPlainString());
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
			if(doRestTemplate(url, json)){
setLastUsd(getUsd());
		setUsd(getUsd().subtract(getRequestBuyPrice().multiply(getRequestedLtc())).setScale(6, RoundingMode.HALF_DOWN));
		if (getUsd().compareTo(new BigDecimal("0")) == -1){
			setUsd(new BigDecimal("0"));
		}
		setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		//Sysout?
		System.out.println("Buy order placed at $" + getRequestBuyPrice());
//		vir.save(vi);
		setDirty(true);
		resetTick();
			} else {
				System.out.println("BUY ORDER FAILED");
			}
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
		} else {

		setLastUsd(getUsd());
		setUsd(getUsd().subtract(getRequestBuyPrice().multiply(getRequestedLtc())).setScale(6, RoundingMode.HALF_DOWN));
		setBuyProcessState("DESIRED_BUY");
		setLifeTimeState("TRADING");
		//Sysout?
		System.out.println("Buy order placed at $" + getRequestBuyPrice());
//		vir.save(vi);
		setDirty(true);
		resetTick();
	}
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
			
			if ((getSimMode() == SimulationMode.SIMULATION)){
				setRequestSellPrice(carrot.getHigh().add(getSlightAmount()));
			} else if ((getSimMode() == SimulationMode.REALTIME)){
				setRequestSellPrice(carrot.getHigh().add(getSlightAmount()));
			}
			
			if (getRequestSellPrice().compareTo(getRequestBuyPrice()) == 1) {
				if (getLastLtc().compareTo(new BigDecimal("0")) == 1){
					setLtc(getLastLtc());
				}
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
					setRequestSellPrice((new BigDecimal(getRequestSellPrice().toPlainString()).setScale(6, RoundingMode.HALF_DOWN)));
					setLtc((new BigDecimal(getLtc().toPlainString()).setScale(5, RoundingMode.HALF_DOWN)));
					order.setPrice(getRequestSellPrice().toPlainString());
					order.setSize(getLtc().toPlainString());
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
					if (doRestTemplate(url, json)){
	setLastLtc(getLtc());
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());
//				vir.save(vi);
				setDirty(true);
				resetTick();
					} else {
						System.out.println("ATTEMPT SELL FAILED");
					}
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
				} else {
				setLastLtc(getLtc());
				setLtc(new BigDecimal("0"));
				//set Litecoin
				setBuyProcessState("DESIRED_SELL");
				setLifeTimeState("TRADING");
				System.out.println("Sell order placed at $" + getRequestSellPrice());
//				vir.save(vi);
				setDirty(true);
				resetTick();
			}
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
		refresh(carrot);
	}
	
	//Implement realtime trading features
	public void refresh(Carrot carrot) {


		if(getBuyProcessState().equals("DESIRED_BUY")) {
			//if current price lower then desired buy then 
			//processBuy(which is buy()), store requested ltc
			//into ltc
			if (getSimMode() == SimulationMode.REALTIME) {
// 				if((getCurrentPrice().subtract(calculateSpread())).compareTo(getRequestBuyPrice()) == -1) {
// 				setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				buy();
// //				vir.save(vi);
// 				setDirty(true);
			
// 			}
// 			else
			 if (getActiveOrder() != null)	{
			
			if (getActiveOrder().getSettled() == true){//if(getCurrentPrice().compareTo(getRequestBuyPrice()) == -1) {
				setPartialState("NONE");
				setLastPartialFill(new BigDecimal("0"));
				buy();
//				vir.save(vi);
				setDirty(true);
			
			} 

			else if (new BigDecimal(getActiveOrder().getFilled_size()).compareTo(new BigDecimal("0")) == 1 && new BigDecimal(getActiveOrder().getFilled_size()).compareTo(getRequestedLtc().subtract(getLastPartialFill())) == -1) {
				// cancelOrder();
				setLastPartialFill(new BigDecimal(getActiveOrder().getFilled_size()));//.add(getLastPartialFill()));
				setPartialState("PARTIAL");
				setDirty(true);
				// deployPartial();
			}

			} 

// 			else if (getOrderId() == null) {
// 				setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				buy();
// //				vir.save(vi);
// 				setDirty(true);
// 			} else if (getActiveOrder() == null){
// 					if (getLifeTimeState().equals("BUY_STUCK")) {
// 						setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				buy();
// //				vir.save(vi);
// 				setDirty(true);
// 					}
// 			}

			} else if(getSimMode() == SimulationMode.SIMULATION) {
				if (getRequestBuyPrice().compareTo(getSimCarrot().getHigh()) == -1) {
					buy();
				}
			}
		} else if (getBuyProcessState().equals("DESIRED_SELL")) {
			if (getSimMode() == SimulationMode.REALTIME) {
				if (getActiveOrder() != null) {
				if (getActiveOrder().getSettled() == true){
					setPartialState("NONE");
				setLastPartialFill(new BigDecimal("0"));
				sell();
//				vir.save(vi);
				setDirty(true);
			} 

// 			else if (getCurrentPrice().compareTo(getRequestSellPrice()) == 1){
// 				setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				sell();
// //				vir.save(vi);
// 				setDirty(true);
// 			} 

			else if (new BigDecimal(getActiveOrder().getFilled_size()).compareTo(new BigDecimal("0")) == 1 && new BigDecimal(getActiveOrder().getFilled_size()).compareTo(getLastLtc().subtract(getLastPartialFill())) == -1) {
				// cancelOrder();

				setLastPartialFill(new BigDecimal(getActiveOrder().getFilled_size()));//.add(getLastPartialFill()));
				setPartialState("PARTIAL");
				setDirty(true);
				// sellPartial();
			} 
			} 

// 			else if (getActiveOrder().getStatus().equals("done")){
// 					if (getLifeTimeState().equals("SELL_STUCK")) {
// 						setPartialState("NONE");
// 				setLastPartialFill(new BigDecimal("0"));
// 				sell();
// //				vir.save(vi);
// 				setDirty(true);
// 					}
// 			} 
			} else if(getSimMode() == SimulationMode.SIMULATION) {
				if (getRequestSellPrice().compareTo(getSimCarrot().getLow()) == 1) {
					sell();
				}
			}
		}
				if (getSimMode() == SimulationMode.REALTIME) {
		if (getBuyProcessState().equals("DESIRED_BUY")){
							if (!fetchOrder()) {

								setLifeTimeState(new String("BUY_STUCK"));
								cancelBuy();
							} 
						}
						if (getBuyProcessState().equals("DESIRED_SELL")){
							if (!fetchOrder()) {
								setLifeTimeState(new String("SELL_STUCK"));
								if (getPartialState().equals("NONE")){
						 		forceLoss();
						 		// attemptSell(carrot);
								} else if (getPartialState().equals("PARTIAL")){
									sellPartial();
						// return;
								}
							} 
						} 

						if (getBuyProcessState().equals("BOUGHT")){
							if (getLifeTimeState().equals("SELL_STUCK")){
							if (getPartialState().equals("NONE")){
						 		forceLoss();
						 		// attemptSell(carrot);
						 
								} else if (getPartialState().equals("PARTIAL")){
									sellPartial();
						// return;
								}
							}


						}

} else {
	if (getBuyProcessState().equals("DESIRED_BUY")){
							//if (!fetchOrder()) {

								setLifeTimeState(new String("BUY_STUCK"));
								cancelBuy();
							//} 
						}
						if (getBuyProcessState().equals("DESIRED_SELL")){
							//if (!fetchOrder()) {
								setLifeTimeState(new String("SELL_STUCK"));
								if (getPartialState().equals("NONE")){
						 		forceLoss();
									// attemptSell(carrot);
						 
								} else if (getPartialState().equals("PARTIAL")){
									sellPartial();
						// return;
								}
							//} 
						}
							if (getBuyProcessState().equals("BOUGHT")){
							if (getLifeTimeState().equals("SELL_STUCK")){
							if (getPartialState().equals("NONE")){
						 		forceLoss();
						 		// attemptSell(carrot);
						 
								} else if (getPartialState().equals("PARTIAL")){
									sellPartial();
						// return;
								}
							}


						}
}

	}
	
	//Ok, now to do sells (start in tradegroup)
	public void buy() {
		timer.cancel();
		setLtc(getRequestedLtc().abs());
		setBuyProcessState("BOUGHT");
		setLifeTimeState("TRADING");
		System.out.println("Bought at $" + getRequestBuyPrice());
//		if (getSimMode() == SimulationMode.REALTIME) {
//		timer.cancel();
//		} else if (getSimMode() == SimulationMode.SIMULATION) {
		// setActiveOrder(null);
		// setOrderId(null);
			resetTick();
			startTimer();
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
		timer.cancel();
		setUsd(getRequestedTotal().abs());
		if (getSimMode() == SimulationMode.REALTIME){


		if ((getUsd().subtract(getLastUsd())).compareTo(new BigDecimal("0")) >= 0){
		setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		setLastUsd(getUsd());
		//profit percentage?
		setBuyProcessState("SOLD");
		setLifeTimeState("RESERVE");
		} 
		else {
			setLoss(getLoss().add((getUsd()).subtract(getLastUsd())));
			setLastUsd(getUsd());
		//profit percentage?
			// setLastUsd(getUsd());
		setBuyProcessState("SOLD");
		setLifeTimeState("IDLE");
		}
		} else {
			if ((getUsd().subtract(getLastUsd())).compareTo(new BigDecimal("0")) >= 0){
		setProfit(getProfit().add(getUsd().subtract(getLastUsd())));
		setLastUsd(getUsd());
		//profit percentage?
		setBuyProcessState("SOLD");
		setLifeTimeState("RESERVE");
		} 
		else {
			setLoss(getLoss().add((getUsd()).subtract(getLastUsd())));
			setLastUsd(getUsd());
		//profit percentage?
			// setLastUsd(getUsd());
		setBuyProcessState("SOLD");
		setLifeTimeState("IDLE");
		}
		}
		setLastLtc(new BigDecimal("0"));
		System.out.println("Sold at $" + getRequestSellPrice());
//		if (getSimMode() == SimulationMode.REALTIME) {
//			timer.cancel();
//			} else if (getSimMode() == SimulationMode.SIMULATION) {
		// setOrderId(null);
		// setActiveOrder(null);
				resetTick();
//			}
				startTimer();
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

	public String getOrderId(){
		return orderId;
	}

	public void setOrderId(String id){
		this.orderId = id;
	}

	public IncomingOrder getActiveOrder(){
		return activeOrder;
	}

	public void setActiveOrder(IncomingOrder activeOrder) {
		this.activeOrder = activeOrder;
	}

	public BigDecimal getLastPartialFill(){
		return lastPartialFill;
	}

	public void setLastPartialFill(BigDecimal lastPartialFill){
		this.lastPartialFill = lastPartialFill;
	}

	public String getPartialState(){
		return partialState;
	}

	public void setPartialState(String partialState){
		this.partialState = partialState;
	}
}
