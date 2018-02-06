package com.mularyanjay.tradeapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
	boolean running;
	
	public AlgorithmManager() {
		//setRunning(false); //redundant, but to be noted
		
		
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
