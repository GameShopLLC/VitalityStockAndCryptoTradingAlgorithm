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
	
	public SimulationManager() {
		checkpointMessages = new ArrayList<String>();
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
			car.setActive(false);
			vitalityInstance.broadcastCarrot(car);
			checkpoint++;
			if (checkpoint == 3599) {
				checkpointMessages.add(vitalityInstance.searchTradeGroupByName("One-1").statusReport());
				checkpoint = 0;
			}
				}
		//if checkpoint = 3599
	}
	
	public List<Candle> getCandleData(){
		List<SerializableCandle> data = new ArrayList<SerializableCandle>();
		HashMap<String, String> map = new HashMap<String, String>();
		ComparableDateTime cdt = new ComparableDateTime("2018-02-1T00:00:00Z");
		for (int i = 0; i < 129; i++) {
			map.clear();
			String from = cdt.toString();
			cdt.incrementMinute(300);
			String to = cdt.toString();
			map.put("from", from);
			map.put("to", to);
			data.addAll(getEpochTimeCandles(map));
		}
		
		List<Candle> candleData = new ArrayList<Candle>();
		for (SerializableCandle s: data) {
			candleData.add((Candle)s);
		}
		Collections.sort(candleData);
		return candleData;
	}
	
	public List<SerializableCandle> getEpochTimeCandles (HashMap <String, String> map) {
		ObjectMapper objectMapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
//1522019164
		//1522019224
		// Sunday, March 25, 2018 11:07:04
		System.out.println("in");
		//"2018-03-25T11:07:04Z"
		String url = "https://api.gdax.com/products/LTC-USD/candles?" + "start=" + map.get("from") + "&end=" + map.get("to") + "&granularity=60";
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