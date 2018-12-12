//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@Controller
public class MainController {

	@Autowired
	@Qualifier("current")
	User currentUser;
	
	@Autowired
	HashingAlgorithm hashingAlgorithm;

	@Autowired
	@Qualifier("main")
	HttpEntityBean httpEntityBean;
	
	@Autowired
	CarrotHistory carrotHistory;
	
	@Autowired
	VitalityInstance vitalityInstance;
	
	@Autowired
	AlgorithmManager algorithmManager;
	
	@Autowired
	SimulationManager simulationManager;
	
	ComparableDateTime lastTime = new ComparableDateTime(0L);
	Carrot currentCarrot;
	
    boolean isLoggedIn;
	int counter = 0;
	String sayHey = "Hey";
	@ModelAttribute("userLogin")
	public User userLogin() {
		return new User();
	}
	
	@RequestMapping(value="/")
	public String index(@RequestParam(value = "errCode", required=false) String errCode, Model model) {
		try {
		if (errCode == null){
			errCode = new String("");
		}
		model.addAttribute("errCode", errCode);
		return "Index";
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String index(@ModelAttribute("userLogin") User userLogin, Model model, BindingResult bindingResult, HttpServletRequest request){
		
		if (this.currentUser.getUsername().equals(userLogin.getUsername()) && this.currentUser.getPassword().equals(userLogin.getPassword())) {
			isLoggedIn = true;
			return "redirect:/main/";
		}
		request.setAttribute("errCode", "inv");
		return "Index";
	
	}
	
	@RequestMapping(value="/main")
	public String main(@RequestParam(value = "errCode", required=false) String errCode, Model model) {
		model.addAttribute("errCode", errCode);
		model.addAttribute("sayHey", sayHey);
		model.addAttribute("hashingAlgorithm", hashingAlgorithm);
		model.addAttribute("algorithmManager", algorithmManager);
		
		//response.setHeader("Access-Control-Allow-Headers", "CB-ACCESS-KEY, CB-ACCESS-SIGN, CB-ACCESS-TIMESTAMP,CB-ACCESS-PASSPHRASE");
	       
		if (isLoggedIn) {
			if(vitalityInstance.getSimMode() == SimulationMode.REALTIME) {
				return "Main";
			} else if (vitalityInstance.getSimMode() == SimulationMode.SIMULATION) {
				return "SimMain";
			}
		}
		
		return "redirect://";
	}
	
	@RequestMapping(value="/testajax")
	public @ResponseBody String testAjax(){
		if (isLoggedIn){
		counter++;
		return "" + counter;
		}
		return "";
	}
	
	//all ajax will be a get request, get or post of api will be handled by backend
	@RequestMapping(value="/testbackendrequest")
	public @ResponseBody String testBackendRequest() {
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
			    new javax.net.ssl.HostnameVerifier(){

			        public boolean verify(String hostname,
			                javax.net.ssl.SSLSession sslSession) {
			            if (hostname.equals("ancient-crag-48261.herokuapp.com")) {
			                return true;
			            }
			            return false;
			        }
			    });
		//if (isLoggedIn){
			ObjectMapper mapper = new ObjectMapper();
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://api.gdax.com/products/ZRX-USD/TICKER";//"https://api.gdax.com/accounts";//"https://api.gdax.com/products/BTC-USD/TICKER";//"https://localhost:8080/testParamType";//"https://api.gdax.com/account";//;
			
			//***NO USE ****
			//ResponseEntity<Account[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<Account[]>(){});//restTemplate.exchange(requestEntity, responseType)//restTemplate.getForEntity(url, String.class)
			//restTemplate.exchange(requestEntity, responseType)//restTemplate.getForEntity(url, String.class)
		//restTemplate.getForEntity(url, String.class);
			//ResponseEntity<List<Account>> response = restTemplate.getfor .exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<List<Account>>(){});//restTemplate.exchange(requestEntity, responseType)//restTemplate.getForEntity(url, String.class);
			//****NO USE END *****
			
			
			//***FOR STRING RESPONSE
			ResponseEntity<String> response;
//			//try {
			
			////response = restTemplate.exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
			
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<String>(){});//restTemplate.exchange(requestEntity, responseType)//
			//***STRING RESPONSE END

			//			//}
			
			//*********ACCOUNT[] RESPONSE*************
//			ResponseEntity<Account[]> response;
////			
//			try {
//			response = restTemplate.exchange(url, HttpMethod.GET, httpEntityBean.getEntityFromUrl(url), new ParameterizedTypeReference<Account[]>(){});//restTemplate.exchange(requestEntity, responseType)//
//			}
//			catch (HttpStatusCodeException e) {
////			    List<String> customHeader = e.getResponseHeaders().get("x-app-err-id");
//////			    List<String> customHeader = e.getResponseHeaders().get("message");
////				   
//////			    String svcErrorMessageID = "";
//////			    if (customHeader != null) {
//////			        svcErrorMessageID = customHeader.get(0);                
//////			    }
//				System.out.println(e.getResponseBodyAsString());
//			    return null;//e.getMessage() + svcErrorMessageID;
//////			    //throw new CustomException(e.getMessage(), e, svcErrorMessageID);
//////			    // You can get the body too but you will have to deserialize it yourself
//////			    // e.getResponseBodyAsByteArray()
//////			    // e.getResponseBodyAsString()
//////			}
//////			catch (Throwable ex) {
//////				return ex.getMessage() + "hai";
//			}
			//*********ACCOUNT[] RESPONSE END*************
			
			//ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			
			//
//			ParameterizedTypeReference <List<Account>> test = new ParameterizedTypeReference<List<Account>>(){};
//			System.out.println(test);
			
//			try {
//				//retur
//				return mapper.writeValueAsString(Arrays.asList(response.getBody()));
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return response.getBody();
			
		//}
			
		//return "";
		//return null;
	}
	
	@RequestMapping(value="/testParamType")
	public @ResponseBody Account[] testParamType() {
		
		Account[] list = new Account[2];
		
		Account account = new Account();
		account.setAvailable(new BigDecimal("1.00"));
		account.setBalance(new BigDecimal("1.00"));
		account.setCurrency("POWER");
		account.setHold(new BigDecimal("0.01"));
		account.setId("trade626");
		account.setProfile_id("1000");
		//return new Account();
		list[0] = account;
		list[1] = account;
		return list;
		
	}
//	
//	@RequestMapping(value="/testParamType")
//	public @ResponseBody List<Account> testParamType() {
//		
//		List<Account> list = new ArrayList<Account>();
//		
//		Account account = new Account();
//		account.setAvailable(new BigDecimal("1.00"));
//		account.setBalance(new BigDecimal("1.00"));
//		account.setCurrency("POWER");
//		account.setHold(new BigDecimal("0.01"));
//		account.setId("trade626");
//		account.setProfile_id("1000");
//		//return new Account();
//		list.add(account);
//		list.add(account);
//		return list;
//		
//	}
//	@RequestMapping(value="/performSimulation", method=RequestMethod.POST)
//	public @ResponseBody String performSimulation()
	@RequestMapping(value="/priceReadResult", method=RequestMethod.POST)
	public @ResponseBody String priceReadResult(@RequestBody TickerData ajaxJSON) {
		//initstarttime
		//4times
		//Or just use last data from hashingalgorithm
		//Or epochsecond? when post requested
		//Use datetime
		//.price - current price (ask)
		//.time -use time parser
		//.bid - highest buy 
		//.ask - lowest sell
		//.volume
		String increaseSignature = "";
		
		try {
		//
		ComparableDateTime cdt = new ComparableDateTime(ajaxJSON.getTime());
		if (currentCarrot == null) {
		currentCarrot = new Carrot(ajaxJSON.getPrice(), cdt);
		try {
		vitalityInstance.broadcastCarrot(currentCarrot);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		}
		
		System.out.println(cdt.toString() + " " + ajaxJSON.getPrice());	
		if (!cdt.toString().equals(lastTime.toString())) {
			if (lastTime != null && cdt != null) {
				if(lastTime.getSecond() != null && cdt.getSecond() != null) {
				if (lastTime.increaseGreater(cdt, "second", new BigDecimal("0"))) {
					increaseSignature = new String("secondIncrease");
					//
					if (currentCarrot != null) {
						currentCarrot.addCurrent(ajaxJSON.getPrice());
						//add current time to carrot HERE
						currentCarrot.addCurrentTime(cdt);
						try {
							vitalityInstance.broadcastCarrot(currentCarrot);
							} catch(Throwable t) {
								t.printStackTrace();
							}
					}
				}
				} else {
					System.out.println("Null Second?");
					increaseSignature = new String("nullsecondstart");
				}
				
				//if(cdt.getMinute() != null) {
				if (lastTime.increaseGreater(cdt, "minute", 0)) {
					increaseSignature = new String(increaseSignature + "minuteIncrease");
					if (currentCarrot != null && !increaseSignature.contains("nullsecondstart")) {
						currentCarrot.closeCarrot(cdt);
						try {
							vitalityInstance.broadcastCarrot(currentCarrot);
							} catch(Throwable t) {
								t.printStackTrace();
							}
						carrotHistory.getHistory().add(currentCarrot);
						currentCarrot = null;
						int i = 1;
						for (Carrot c: carrotHistory.getHistory()) {
							System.out.println("" + i + ")" + c.toString());
						}
						//need global boolean to open and 
						//close carrot... er to decide to make
						//new carrot.  Or can reset to null
						//o.O
						
					}
				}
				//}
				//if ()
			}
			lastTime.setDateTime(cdt.toString());
			//return cdt.toString() + " " + ajaxJSON.getPrice() + " " + increaseSignature + " "+ "true";
			//return "" + 
//			try {
//			if (currentCarrot != null) {
//				
//				return currentCarrot.toString();
//			} else {
//				if (carrotHistory.getHistory().size() > 0) {
//				return carrotHistory.getHistory().get(carrotHistory.getHistory().size() - 1).toString();
//				} else {
//					return "No carrots available????";
//				}
//			}
//			} catch (Throwable t) {
//				t.printStackTrace();
//			}
		}
		lastTime.setDateTime(cdt.toString());
		//return "";//cdt.toString() + " " + ajaxJSON.getPrice() + " " + "false";	
		try {
			if (currentCarrot != null) {
				
				return currentCarrot.toString();
			} else {
				if (carrotHistory.getHistory().size() > 0) {
				return carrotHistory.getHistory().get(carrotHistory.getHistory().size() - 1).toString();
				} else {
					return "No carrots available????";
				}
			}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		return "No Result";
	}
	
	//Return all of the reports o.O
	@RequestMapping(value="/vitalityInstanceStatus")
	public @ResponseBody String vitalityInstanceStatus() {
		StringBuilder tradeGroupStatusReports = new StringBuilder();
		
		for (TradeGroup g: vitalityInstance.getGroups()) {
			tradeGroupStatusReports.append(g.statusReport() + "\n");
		}
		return vitalityInstance.entryPointReport() + "\n" +
			   vitalityInstance.carrotsReport() + "\n" +
			   vitalityInstance.statusReport() + "\n" +
			   tradeGroupStatusReports.toString();
	}
	
	@RequestMapping(value="/startAlgorithm", method=RequestMethod.POST)
	public @ResponseBody String startAlgorithm() {
		algorithmManager.setRunning(true);
		//vitalityInstance.restartTradeGroups();
		return Boolean.toString(algorithmManager.isRunning());
	}
	
	@RequestMapping(value="/rallyAlgorithm", method=RequestMethod.POST)
	public @ResponseBody String rallyAlgorithm() {
		vitalityInstance.triggerRally();
		//vitalityInstance.
		return Boolean.toString(algorithmManager.isRunning());
	}
	
	@RequestMapping(value="/restartAlgorithm", method=RequestMethod.POST)
	public @ResponseBody String restartAlgorithm() {
		vitalityInstance.restartTradeGroups();
		//vitalityInstance.
		return Boolean.toString(algorithmManager.isRunning());
	}
	
	
	@RequestMapping(value="/isAlgorithmRunning")
	public @ResponseBody String isAlgorithmRunning() {
		return Boolean.toString(algorithmManager.isRunning());
	}
	
	@RequestMapping(value="/showTickerData")
	public @ResponseBody String showTickerData() {
		
		return algorithmManager.gettData();
	}
	
	@RequestMapping(value="/showPrice")
	public @ResponseBody String showPrice() {
	
		return algorithmManager.getLtcPrice();
	}
	
	@RequestMapping(value="/showCarrotData")
	public @ResponseBody String showCarrotData() {
		return algorithmManager.getCarrotData();
	}
	
	//Note, can only handle a single trade group at the time
	@RequestMapping(value="/showThreads")
	public @ResponseBody List<SerializableTradeThread> showThreads() {
		List<SerializableTradeThread> threads = new ArrayList<SerializableTradeThread>();
		for(TradeGroup g: vitalityInstance.getGroups()) {
			for(TradeThread t: g.getTrades()) {
				SerializableTradeThread stt = new SerializableTradeThread();
				stt.setName(g.getName());
				stt.setBuyProcessState(t.getBuyProcessState());
				stt.setCurrentPrice(t.getCurrentPrice());
				stt.setLastUsd(t.getLastUsd());
				stt.setLifeTimeState(t.getLifeTimeState());
				stt.setLtc(t.getLtc());
				stt.setProfit(t.getProfit());
				stt.setRequestBuyPrice(t.getRequestBuyPrice());
				stt.setRequestedLtc(t.getRequestedLtc());
				stt.setRequestedTotal(t.getRequestedTotal());
				stt.setRequestSellPrice(t.getRequestSellPrice());
				stt.setUsd(t.getUsd());
				threads.add(stt);
			}
		}
		
		return threads;
	}
	
	@RequestMapping(value="/showEpochTimeCandle", method=RequestMethod.POST)
	public @ResponseBody List<SerializableCandle> showEpochTimeCandle(@RequestBody HashMap<String, String> map) {
		
		return simulationManager.getEpochTimeCandles(map);
		
	}
	@RequestMapping(value="/runSimulation", method=RequestMethod.POST)
	public @ResponseBody String runSimulation() {

		simulationManager.runSimulation();
		return "done";
	}
	
	@RequestMapping(value="/simulationStatusReport")
	public @ResponseBody String simulationStatusReport() {
		StringBuilder t = new StringBuilder();
		
		for (String s: simulationManager.checkpointMessages) {
			t.append(s + "\n");
		}
		return t.toString();
	}
	
	@RequestMapping(value="/gdaxData")
	public @ResponseBody String gdaxData() {
		StringBuilder t = new StringBuilder();
		
		for (String s: simulationManager.getRequestMessages) {
			t.append(s + "\n");
		}
		return t.toString();
	}
}
