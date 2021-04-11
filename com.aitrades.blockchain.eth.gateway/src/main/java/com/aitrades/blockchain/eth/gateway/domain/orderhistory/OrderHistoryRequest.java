package com.aitrades.blockchain.eth.gateway.domain.orderhistory;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

public class OrderHistoryRequest {

	private String id;
	private String ethWalletPublicKey;
	private String bscWalletPublicKey;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEthWalletPublicKey() {
		return ethWalletPublicKey;
	}
	public void setEthWalletPublicKey(String ethWalletPublicKey) {
		this.ethWalletPublicKey = ethWalletPublicKey;
	}
	public String getBscWalletPublicKey() {
		return bscWalletPublicKey;
	}
	public void setBscWalletPublicKey(String bscWalletPublicKey) {
		this.bscWalletPublicKey = bscWalletPublicKey;
	}
	@JsonIgnore
	public List<String> getWalletIds(){
		return Lists.newArrayList(this.ethWalletPublicKey, this.bscWalletPublicKey);
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
