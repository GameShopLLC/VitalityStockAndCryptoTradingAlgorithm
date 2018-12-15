package com.mularyanjay.tradeapp;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;

@AccessType(Type.FIELD)
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
	
	public ComparableDateTime(long time) {
		Date date = new Date(time * 1000L);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatted = df.format(date);
		String[] updateFormat = formatted.split(" ");
		setDateTime(updateFormat[0] + "T" + updateFormat[1] + "Z");
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
	
	public void incrementSecond(int amount) {
		if ((getSecond().add(new BigDecimal("" + amount))).compareTo(new BigDecimal("60")) == -1) {
			setSecond(getSecond().add(new BigDecimal("" + amount)));
			
		} else {
			int remainder = 0;
			int minuteAdd = 0;
			remainder = amount % 60;
			minuteAdd = (amount - remainder) / 60;
			setSecond(getSecond().add(new BigDecimal("" + remainder)));
			incrementMinute(minuteAdd);
			
		}
	}
	
	public void incrementMinute(int amount) {
	
		if ((getMinute() + amount) < 60) {
			setMinute (getMinute() + amount);
		} else {
			int remainder = 0;
			int hourAdd = 0;
			if (amount >= 60) {
			remainder = amount % 60;
			hourAdd = (amount - remainder) / 60;
			setMinute(getMinute() + remainder);
			incrementHour(hourAdd);
			} else {
				remainder = (getMinute() + amount) % 60;
				setMinute(0);
				setMinute(remainder);
				incrementHour(1);
			}
		}
	}
	
	public void incrementHour(int amount) {
		if ((getHour() + amount) < 24) {
			setHour (getHour() + amount);
		} else {
			int remainder = 0;
			int dayAdd = 0;
			if (amount >= 24) {
			remainder = amount % 24;
			dayAdd = (amount - remainder) / 24;
			setHour(getHour() + remainder);
			incrementDay(dayAdd);
			} else {
				remainder = (getHour() + amount) % 24;
				setHour(0);
				setHour(remainder);
				incrementDay(1);
			}
		}
	}
	
	public void incrementDay(int amount) {
		int monthDays = 0;
		
		switch(getMonth()) {
		case 1:
			monthDays = 31;
			break;
		case 3:
			monthDays = 31;
			break;
		case 5:
			monthDays = 31;
			break;
		case 7:
			monthDays = 31;
			break;
		case 8:
			monthDays = 31;
			break;
		case 10:
			monthDays = 31;
			break;
		case 12:
			monthDays = 31;
			break;
		case 2:
			monthDays = 28;
			break;
		case 4:
			monthDays = 30;
			break;
		case 6:
			monthDays = 30;
			break;
		case 9:
			monthDays = 30;
			break;
		case 11:
			monthDays = 30;
			break;
		}
		if ((getDay() + amount) <= monthDays) {
			setDay (getDay() + amount);
		} else {
			int remainder = 0;
			int monthAdd = 0;
			if (amount >= monthDays) {
			remainder = amount % monthDays;
			monthAdd = (amount - remainder) / monthDays;
			setDay(getDay() + remainder);
			incrementMonth(monthAdd);
			} else {
				remainder = (getDay() + amount) % monthDays;
				setDay(0);
				setDay(remainder);
				incrementMonth(1);
			}
		}
	}
	
	public void incrementMonth(int amount) {
		if ((getMonth() + amount) <= 12) {
			setMonth (getMonth() + amount);
		} else {
			int remainder = 0;
			int yearAdd = 0;
			if (amount >= 12) {
			remainder = getMonth() % 12;
			yearAdd = (getMonth() - remainder) / 12;
			setMonth(getMonth() + remainder);
			incrementYear(yearAdd);
			} else {
				remainder = (getMonth() + amount) % 12;
				setMonth(0);
				setMonth(remainder);
				incrementYear(1);
			}
		}
	}
	
	public void incrementYear(int amount) {
		setYear(getYear() + amount);
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
		String secondString;
		String minuteString;
		String hourString;
		String dayString;
		String monthString;
		if (getSecond().compareTo(new BigDecimal("10")) == -1) {
			secondString = "0" + getSecond().toString();
		} else {
			secondString = getSecond().toString();
		}
		
		if (getMinute() < 10) {
			minuteString = "0" + getMinute();
		} else {
			minuteString = "" + getMinute();
		}
		
		if (getHour() < 10) {
			hourString = "0" + getHour();
		} else {
			hourString = "" + getHour();
		}
		
		if (getDay() < 10) {
			dayString = "0" + getDay();
		} else {
			dayString = "" + getDay();
		}
		
		if (getMonth() < 10) {
			monthString = "0" + getMonth();
		} else {
			monthString = "" + getMonth();
		}
		return "" + getYear() + "-" + monthString + "-" + dayString + "T" + hourString + ":" + minuteString + ":" + secondString + "Z";
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
