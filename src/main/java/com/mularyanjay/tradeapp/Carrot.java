//Copyright (C) 2017 Lynden Jay Evans Jr

//I, LYNDEN JAY EVANS JR, AM THE CREATOR OF THIS
//SOURCE CODE, AND UNDER 17 U.S.C. §§ 101-810
//COPYRIGHT LAW, NO ONE OTHER THAN ME MAY VIEW, 
//EXECUTE, DISTRIBUTE, OR SELL THIS CODE. 

package com.mularyanjay.tradeapp;

import java.math.BigDecimal;

public class Carrot {
	
	private BigDecimal open;
	private BigDecimal current;
	private BigDecimal close;
	private BigDecimal high;
	private BigDecimal low;
	private boolean active;
	private String trend; 
	private ComparableDateTime startTime;
	private ComparableDateTime endTime;
	
	//INC, DEC, EQ
	//Last carrot, current carrot, carrot history
	//be sure to null check endtime if carrot is not
	//closed, or to check if carrot closed before
	//checking endtime
	
	public Carrot() {
		
	}

	public Carrot(BigDecimal start, ComparableDateTime newStartTime) {
		
		startTime = new ComparableDateTime(newStartTime.toString());
		setActive(true);
		setOpen(start);
		setCurrent(start);
		setHigh(start);
		setLow(start);
	}
	
	public void addCurrent(BigDecimal newCurrent) {
		if (isActive()) {
			setCurrent(newCurrent);
			if (getCurrent().compareTo(getHigh()) == 1) {
				setHigh(newCurrent);
			} else if (getCurrent().compareTo(getLow()) == -1) {
				setLow(newCurrent);
			}
			
			if(getOpen().subtract(getCurrent()).compareTo(new BigDecimal("0")) == 1) {
				setTrend("DEC");
			} else if (getOpen().subtract(getCurrent()).compareTo(new BigDecimal("0")) == -1) {	
				setTrend("INC");
			} else {
				setTrend("EQ");
			}
			
		}
	}
	
	public void closeCarrot(ComparableDateTime newEndTime) {
		setClose(getCurrent());
		endTime = new ComparableDateTime(newEndTime.toString());
		setActive(false);
	}
	
	public String toString() {
		String close = "undefined";
		if (getClose() != null) {
			close = "" + getClose();
		}
		return "[open " + getOpen() +", current "+ getCurrent() + ", high " + getHigh() + ", low " + getLow() + ", close " + close + ", trend " + getTrend() +"]";
	}
	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getCurrent() {
		return current;
	}

	public void setCurrent(BigDecimal current) {
		this.current = current;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	public ComparableDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(ComparableDateTime startTime) {
		this.startTime = startTime;
	}

	public ComparableDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(ComparableDateTime endTime) {
		this.endTime = endTime;
	}
	
	
}