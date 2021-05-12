package com.aitrades.blockchain.eth.gateway.domain.orderhistory;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class OrderHistory {

	private String id;
	private String orderId;
	private String route;
	private String tradetype;
	private String fromTickerSymbol;
	private String input;
	private String toTickerSymbol;
	private String output;
	private String executedprice;
	private String orderstate;
	private String approvedhash;
	private String approvedhashStatus;
	private String swappedhash;
	private String swappedhashStatus;
	private String orderside;
	private String errormessage;
	private String slipage;
	private String gasLimit;
	private String gasPrice;
	private String toTickerAddress;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getTradetype() {
		return tradetype;
	}
	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}
	public String getFromTickerSymbol() {
		return fromTickerSymbol;
	}
	public void setFromTickerSymbol(String fromTickerSymbol) {
		this.fromTickerSymbol = fromTickerSymbol;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getToTickerSymbol() {
		return toTickerSymbol;
	}
	public void setToTickerSymbol(String toTickerSymbol) {
		this.toTickerSymbol = toTickerSymbol;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getExecutedprice() {
		return executedprice;
	}
	public void setExecutedprice(String executedprice) {
		this.executedprice = executedprice;
	}
	public String getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(String orderstate) {
		this.orderstate = orderstate;
	}
	public String getApprovedhash() {
		return approvedhash;
	}
	public void setApprovedhash(String approvedhash) {
		this.approvedhash = approvedhash;
	}
	public String getApprovedhashStatus() {
		return approvedhashStatus;
	}
	public void setApprovedhashStatus(String approvedhashStatus) {
		this.approvedhashStatus = approvedhashStatus;
	}
	public String getSwappedhash() {
		return swappedhash;
	}
	public void setSwappedhash(String swappedhash) {
		this.swappedhash = swappedhash;
	}
	public String getSwappedhashStatus() {
		return swappedhashStatus;
	}
	public void setSwappedhashStatus(String swappedhashStatus) {
		this.swappedhashStatus = swappedhashStatus;
	}
	public String getOrderside() {
		return orderside;
	}
	public void setOrderside(String orderside) {
		this.orderside = orderside;
	}
	public String getErrormessage() {
		return errormessage;
	}
	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}
	
	public String getSlipage() {
		return slipage;
	}

	public void setSlipage(String slipage) {
		this.slipage = slipage;
	}

	public String getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
	}

	public String getGasPrice() {
		return gasPrice;
	}

	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}

	public String getToTickerAddress() {
		return toTickerAddress;
	}

	public void setToTickerAddress(String toTickerAddress) {
		this.toTickerAddress = toTickerAddress;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
}
