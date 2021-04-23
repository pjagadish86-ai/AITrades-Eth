package com.aitrades.blockchain.eth.gateway.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)	
@JsonInclude(Include.NON_NULL)
public class RetriggerSnipeOrder {
	
	private String parentSnipeOrderId;
	private String snipeOrderId;
	private BigDecimal slipage;
	private Double slipageInDouble;
	private BigInteger gasPrice;
	private BigInteger gasLimit;
	
	public String getParentSnipeOrderId() {
		return parentSnipeOrderId;
	}
	public void setParentSnipeOrderId(String parentSnipeOrderId) {
		this.parentSnipeOrderId = parentSnipeOrderId;
	}
	public BigDecimal getSlipage() {
		return slipage;
	}
	public void setSlipage(BigDecimal slipage) {
		this.slipage = slipage;
	}
	public Double getSlipageInDouble() {
		return slipageInDouble;
	}
	public void setSlipageInDouble(Double slipageInDouble) {
		this.slipageInDouble = slipageInDouble;
	}
	public BigInteger getGasPrice() {
		return gasPrice;
	}
	public void setGasPrice(BigInteger gasPrice) {
		this.gasPrice = gasPrice;
	}
	public BigInteger getGasLimit() {
		return gasLimit;
	}
	public void setGasLimit(BigInteger gasLimit) {
		this.gasLimit = gasLimit;
	}
	public String getSnipeOrderId() {
		return snipeOrderId;
	}
	public void setSnipeOrderId(String snipeOrderId) {
		this.snipeOrderId = snipeOrderId;
	}

}
