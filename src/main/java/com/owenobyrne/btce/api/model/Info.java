package com.owenobyrne.btce.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

/*
 * @author Owen
 *
 */
public class Info {
	String success;
	@JsonProperty("return")
	InfoDatum data;
	
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public InfoDatum getReturn() {
		return data;
	}
	public void setReturn(InfoDatum data) {
		this.data = data;
	}
	
	
}
