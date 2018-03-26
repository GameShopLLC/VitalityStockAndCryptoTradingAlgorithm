package com.mularyanjay.tradeapp;

public class Candle extends SerializableCandle implements Comparable<Candle> {

	public Candle() {
		
	}

	@Override
	public int compareTo(Candle candle) {
		// TODO Auto-generated method stub
		long compareTime = Long.parseLong(candle.getTime());
		
		return (int)(Long.parseLong(this.getTime()) - compareTime);
	}
	
	
}
