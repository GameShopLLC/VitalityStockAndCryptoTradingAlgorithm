package com.mularyanjay.tradeapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SimulationManager {

	@Autowired
	@Qualifier("local")
	HttpEntityBean localEntityBean;
	
	@Autowired
	VitalityInstance vitalityInstance;
	
	List<String> checkpointMessages;
	
	public SimulationManager() {
		checkpointMessages = new ArrayList<String>();
	}
	
	public void runSimulation(List<Candle> data) {
		int checkpoint = 0;
		for (Candle c: data) {
			Carrot car = new Carrot();
			car.setOpen(c.getOpen());
			car.setClose(c.getClose());
			car.setLow(c.getLow());
			car.setHigh(c.getHigh());
			
		}
		//if checkpoint = 3599
	}
}
