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
	
	public boolean slotEquals(String timeslot, int equals) {
		switch(timeslot) {
		case "year":
			if (getYear() == equals) {
				return true;
			}
			return false;
		case "month":
			if (getMonth() == equals) {
				return true;
			}	
			return false;
		case "day":
			if (getDay() == equals) {
				return true;
			}	
			return false;
		case "hour":
			if (getHour() == equals) {
				return true;
			}	
			return false;
		case "minute":
			if (getMinute() == equals) {
				return true;
			}	
			return false;
		}
		return false;
	}
	
	public boolean slotEquals(String timeslot, BigDecimal equals) {
		switch(timeslot) {
		case "second":
			if (getSecond().compareTo(equals) == 0) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean increaseEquals(ComparableDateTime other, String timeslot, int increase) {
		
		switch(timeslot) {
		case "year":
			if (other.getYear() == getYear() + increase) {
				return true;
			}	
			return false;
			//break;
		case "month":
			if (other.getMonth() == getMonth() + increase) {
				return true;
			}	
			return false;
		case "day":
			if (other.getDay() == getDay() + increase) {
				return true;
			}	
			return false;
		case "hour":
			if (other.getHour() == getHour() + increase) {
				return true;
			}	
			return false;
		case "minute":
			if (other.getMinute() == getMinute() + increase) {
				return true;
			}	
			return false;
		}
		return false;
	}
	
	public boolean increaseEquals(ComparableDateTime other, String timeslot, BigDecimal increase) {
		
		switch(timeslot) {
		case "second":
			if (other.getSecond() == getSecond().add(increase)) {
				return true;
			}	
			return false;
		}
		return false;
	}
	
public boolean increaseGreater(ComparableDateTime other, String timeslot, int increase) {
		
		switch(timeslot) {
		case "year":
			if (other.getYear() > getYear() + increase) {
				return true;
			}	
			return false;
			//break;
		case "month":
			if (other.getMonth() > getMonth() + increase) {
				return true;
			}	
			return false;
		case "day":
			if (other.getDay() > getDay() + increase) {
				return true;
			}	
			return false;
		case "hour":
			if (other.getHour() > getHour() + increase) {
				return true;
			}	
			return false;
		case "minute":
			if (other.getMinute() > getMinute() + increase) {
				return true;
			}	
			return false;
		}
		return false;
	}
	
	public boolean increaseGreater(ComparableDateTime other, String timeslot, BigDecimal increase) {
		
		switch(timeslot) {
		case "second":
			if (other.getSecond().compareTo(getSecond().add(increase)) == 1) {
				return true;
			}	
			return false;
		}
		return false;
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
