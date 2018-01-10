package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class ComparableDateTime {

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private BigDecimal second;
	
	public ComparableDateTime() {
		
	}
	
	public ComparableDateTime(String epochDateTime) {
		setDateTime(epochDateTime);
	}
	
	public void setDateTime (String epochDateTime) {
		String[] parts = new String[2];
		parts = epochDateTime.split("T");
		String[] date = new String[3];
		String[] time = new String[3];
		date = parts[0].split("-");
		time = parts[1].split(":");
		setYear(Integer.valueOf(date[0]));
		setMonth(Integer.valueOf(date[1]));
		setDay(Integer.valueOf(date[2]));
		setHour(Integer.valueOf(time[0]));
		setMinute(Integer.valueOf(time[1]));
		setSecond(BigDecimal.valueOf(Double.valueOf(time[2].substring(0, time[2].length() - 1))));
		
	}
	
	public String toString() {
		return "" + getYear() + "-" + getMonth() + "-" + getDay() + "T" + getHour() + ":" + getMinute() + ":" + getSecond() + "Z";
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public BigDecimal getSecond() {
		return second;
	}

	public void setSecond(BigDecimal second) {
		this.second = second;
	}
	
}
