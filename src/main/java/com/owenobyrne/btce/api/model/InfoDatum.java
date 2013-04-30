package com.owenobyrne.btce.api.model;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author Owen
 *
 */
public class InfoDatum {
	HashMap<String, BigDecimal> funds;
	HashMap<String, Integer> rights;
	int transaction_count;
	int open_orders;
	long server_time;
	
	public HashMap<String, BigDecimal> getFunds() {
		return funds;
	}
	public void setFunds(HashMap<String, BigDecimal> funds) {
		this.funds = funds;
	}
	public HashMap<String, Integer> getRights() {
		return rights;
	}
	public void setRights(HashMap<String, Integer> rights) {
		this.rights = rights;
	}
	public int getTransaction_count() {
		return transaction_count;
	}
	public void setTransaction_count(int transaction_count) {
		this.transaction_count = transaction_count;
	}
	public int getOpen_orders() {
		return open_orders;
	}
	public void setOpen_orders(int open_orders) {
		this.open_orders = open_orders;
	}
	public long getServer_time() {
		return server_time;
	}
	public void setServer_time(long server_time) {
		this.server_time = server_time;
	}
	
	
}
