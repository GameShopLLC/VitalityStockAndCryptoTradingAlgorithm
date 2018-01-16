//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class CarrotHistory {

	private ArrayList<Carrot> history;
	
	public CarrotHistory() {
		this.history = new ArrayList<Carrot>();
		
	}

	//Add to database here if database implemented
	public ArrayList<Carrot> getHistory() {
		if (history.size() > 5) {
			history.remove(0);
		}
		return history;
	}

	public void setHistory(ArrayList<Carrot> history) {
		this.history = history;
	}
	
}
