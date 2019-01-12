package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SimulationManager {

	@Autowired
	@Qualifier("local")
	HttpEntityBean localEntityBean;
	
	@Autowired
	@Qualifier("main")
	HttpEntityBean httpEntityBean;
	
	@Autowired
	VitalityInstance vitalityInstance;
	
	List<String> checkpointMessages;
	List<String> getRequestMessages;
	
	public SimulationManager() {
		checkpointMessages = new ArrayList<String>();
		getRequestMessages = new ArrayList<String>();
	}
	
	public void runSimulation() {
		List<Candle> data = new ArrayList<Candle>();
		data = getCandleData();
		int checkpoint = 0;
		
		for (Candle c: data) {
			Carrot car = new Carrot();
			car.setOpen(c.getOpen());
			car.setClose(c.getClose());
			car.setLow(c.getLow());
			car.setHigh(c.getHigh());
			car.setCurrent(c.getClose());
			car.setActive(false);
			car.evaluateTrend();
			long oldTime = c.getTime() - 1800L;
			car.setStartTime(new ComparableDateTime(oldTime));
			car.setCurrentTime(new ComparableDateTime(c.getTime()));
			car.setEndTime(new ComparableDateTime(c.getTime()));
			System.out.println(car.toString());
			vitalityInstance.broadcastCarrot(car);
			checkpoint++;
			if (checkpoint > 1799) {
				checkpointMessages.add(vitalityInstance.searchTradeGroupByName("One-1").statusReport());
				System.out.println(vitalityInstance.searchTradeGroupByName("One-1").statusReport());
				checkpoint = 0;
			}
//			if (vitalityInstance.searchTradeGroupByName("One-1").getState().equals("ACTIVE") && (vitalityInstance.searchTradeGroupByName("One-1").getBuyStuckCount() + vitalityInstance.searchTradeGroupByName("One-1").getSellingThreadCount() >= 32)) {//14400)) {//1800)) {//7200)) {//3600)) {
//				vitalityInstance.triggerRally();
//			} else if (vitalityInstance.searchTradeGroupByName("One-1").getState().equals("RALLYING") && (vitalityInstance.searchTradeGroupByName("One-1").getSellingThreadCount() - vitalityInstance.searchTradeGroupByName("One-1").getSellStuckCount()) == 0) {
//				vitalityInstance.cancelAllRallies();
//			}
			
//			if (vitalityInstance.getProfit().compareTo(new BigDecimal("14000000")) == 1) {
//				vitalityInstance.triggerRally();
//			}
			//if (vitalityInstance.searchTradeGroupByName("One-1").get)
				}
		//if checkpoint = 3599
	}
	
	public List<Candle> getCandleData(){
		ObjectMapper objectMapper = new ObjectMapper();
		
		List<SerializableCandle> data = new ArrayList<SerializableCandle>();
		HashMap<String, String> map = new HashMap<String, String>();
		ComparableDateTime cdt = new ComparableDateTime("2018-12-25T00:00:00Z");
		for (int i = 0; i < 50; i++) { //1882 for a year //273 for feb and march //153 march 144 feb //450 jan to april
			map.clear();
			String from = cdt.toString();
			cdt.incrementMinute(300);
			String to = cdt.toString();
			map.put("from", from);
			map.put("to", to);
			try {
				Thread.sleep(1500L);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			data.addAll(getEpochTimeCandles(map));
		}
		
		List<Candle> candleData = new ArrayList<Candle>();
		for (SerializableCandle s: data) {
			Candle c = new Candle();
			c.setClose(s.getClose());
			c.setHigh(s.getHigh());
			c.setLow(s.getLow());
			c.setOpen(s.getOpen());
			c.setTime(s.getTime());
			c.setVolume(s.getVolume());
			//System.out.println("candle array at " + c.getTime());
			candleData.add(c);
		}
		Collections.sort(candleData);
		System.out.println("donewitharray");
//		try {
//			System.out.println(objectMapper.writeValueAsString(Arrays.asList(candleData)));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return candleData;
	}
	
	public List<SerializableCandle> getEpochTimeCandles (HashMap <String, String> map) {
		ObjectMapper objectMapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
//1522019164
		//1522019224
		// Sunday, March 25, 2018 11:07:04
		System.out.println("in");
//		try {
//			Thread.sleep(1000L);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//"2018-03-25T11:07:04Z"
		String url = "https://api.gdax.com/products/ZRX-USD/candles?" + "start=" + map.get("from") + "&end=" + map.get("to") + "&granularity=60";
		//getRequestMessages.add(url);
		ResponseEntity<List<List<String>>> response = null;

		System.out.println("mid");
		try {
		response = restTemplate.exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<List<List<String>>>(){});//restTemplate.exchange(requestEntity, responseType)//

		System.out.println("try");
		} catch(Throwable t) {
			t.printStackTrace();
		}
		System.out.println("out");

		try {
			System.out.println(objectMapper.writeValueAsString(Arrays.asList(response.getBody())));
			//getRequestMessages.add(objectMapper.writeValueAsString(Arrays.asList(response.getBody())));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//settData(response.getBody());
		//TickerData tickerData = null;
//		try {
//			tickerData = objectMapper.readValue(response.getBody(), TickerData.class);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if (tickerData != null) {
//			//setLtcPrice("The current price of litecoin is " + tickerData.getPrice());
//			url = new String("https://ancient-crag-48261.herokuapp.com/priceReadResult");
//			response = restTemplate.exchange(url, HttpMethod.POST, localHttpEntityBean.postLocalEntityFromUrl(url, "application/json", "text", tickerData),new ParameterizedTypeReference<String>(){});
//			//setCarrotData(response.getBody());
//		} else {
//			//setLtcPrice("The current price of litecoin is undefined");
//		}
		List<SerializableCandle> list = new ArrayList<SerializableCandle>();
		
		for (List<String> s: response.getBody()) {
			SerializableCandle candle = new SerializableCandle();
			candle.setTime(Long.parseLong(s.get(0)));
			candle.setLow(new BigDecimal(s.get(1)));
			candle.setHigh(new BigDecimal(s.get(2)));
			candle.setOpen(new BigDecimal(s.get(3)));
			candle.setClose(new BigDecimal(s.get(4)));
			candle.setVolume(new BigDecimal(s.get(5)));
//			private BigDecimal low;
//			private BigDecimal high;
//			private BigDecimal open;
//			private BigDecimal close;
//			private BigDecimal volume;
			list.add(candle);
		}
		return list;
		
		
	}
}
