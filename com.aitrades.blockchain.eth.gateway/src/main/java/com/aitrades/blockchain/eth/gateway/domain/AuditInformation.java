package com.aitrades.blockchain.eth.gateway.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class AuditInformation {

	private String createdDateTime;
	private String updatedDateTime;
	
	public AuditInformation(String createdDateTime, String updatedDateTime) {
		this.createdDateTime = createdDateTime;
		this.updatedDateTime = updatedDateTime;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public String getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(String updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

}
