package com.aitrades.blockchain.gateway.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OrderRes {
	
	private String messageSuccess;
	
	@JsonCreator
	public OrderRes(@JsonProperty("messageSuccess") String messageSuccess) {
		this.messageSuccess = messageSuccess;
	}

	public String getMessageSuccess() {
		return messageSuccess;
	}


}
