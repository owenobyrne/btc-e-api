package com.owenobyrne.btce.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Depth {
	ArrayList<ArrayList<BigDecimal>> asks;
	ArrayList<ArrayList<BigDecimal>> bids;
	
	public ArrayList<ArrayList<BigDecimal>> getAsks() {
		return asks;
	}
	public void setAsks(ArrayList<ArrayList<BigDecimal>> asks) {
		this.asks = asks;
	}
	public ArrayList<ArrayList<BigDecimal>> getBids() {
		return bids;
	}
	public void setBids(ArrayList<ArrayList<BigDecimal>> bids) {
		this.bids = bids;
	}
	
	
	
}
