//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AlgorithmManager {

	//This needs a boolean to see if algorithm is running
	//Do not add RALLYING state yet
	//Algorithm cannot be fed carrot data (this is for
	//entry point) if this says its not running
	//If its on, it will use timers/httpentities
	@Autowired
	@Qualifier("local")
	HttpEntityBean localHttpEntityBean;
	private boolean running;
	private Timer timer;
	private String ltcPrice;
	private String tData;
	private String carrotData;
	
	public AlgorithmManager() {
		//setRunning(false); //redundant, but to be noted
		
		
	}

	public void runAlgorithm() {
		setTimer(new Timer());
		getTimer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
//				if (getBuyProcessState().equals("DESIRED_BUY")) {
//					setLifeTimeState("BUY_STUCK");
//				}
				//timer.cancel();
				try {
					putTickerData();
				} catch (Throwable t) {
					t.printStackTrace();
					runAlgorithm();
				}
				
				
			}
			
		}, 0, 1000);
		
	}
	
	public void putTickerData(){


		ObjectMapper objectMapper = new ObjectMapper();
				RestTemplate restTemplate = new RestTemplate();
				String url = "https://ancient-crag-48261.herokuapp.com/testbackendrequest";
				ResponseEntity<String> response = null;
				//try{
					response = restTemplate.exchange(url, HttpMethod.GET, localHttpEntityBean.getLocalEntityFromUrl(url,"application/json"), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
					settData(response.getBody());
				// } catch (Throwable t) {
				// 	t.printStackTrace();
				// 	putTickerData();
				// }
				
				TickerData tickerData = null;
				try {
					tickerData = objectMapper.readValue(response.getBody(), TickerData.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (tickerData != null) {
					//try{
						putCarrotData(tickerData);
					// } catch (Throwable t) {
					// 	t.printStackTrace();
					// 	putCarrotData(tickerData);
					// }
					
							} else {
					setLtcPrice("The current price of litecoin is undefined");
				}
	}

	public void putCarrotData(TickerData tickerData) {
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> response = null;

					setLtcPrice("The current price of litecoin is " + tickerData.getPrice());
					String url = new String("https://ancient-crag-48261.herokuapp.com/priceReadResult");
					//try{
					response = restTemplate.exchange(url, HttpMethod.POST, localHttpEntityBean.postLocalEntityFromUrl(url, "application/json", "text", tickerData),new ParameterizedTypeReference<String>(){});
					setCarrotData(response.getBody());
					// } catch (Throwable t){
					// 	t.printStackTrace();
					// 	putCarrotData(tickerData);
					// }
					
		
	}
//	public void buy() {
//		
//	}
//	
//	public void sell() {
//		
//	}
	public void stopAlgorithm() {
		timer.cancel();
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		if (running) {
			runAlgorithm();
		} else {
			stopAlgorithm();
		}
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public String getLtcPrice() {
		return ltcPrice;
	}

	public void setLtcPrice(String ltcPrice) {
		this.ltcPrice = ltcPrice;
	}

	public String gettData() {
		return tData;
	}

	public void settData(String tData) {
		this.tData = tData;
	}

	public String getCarrotData() {
		return carrotData;
	}

	public void setCarrotData(String carrotData) {
		this.carrotData = carrotData;
	}
	
	

}
