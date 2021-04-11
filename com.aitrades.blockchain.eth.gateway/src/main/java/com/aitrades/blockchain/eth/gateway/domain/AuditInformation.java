package com.aitrades.blockchain.eth.gateway.domain;

import java.time.LocalDateTime;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class AuditInformation {

	private String createdDateTime;
	private String updatedDateTime;
	
	public String getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		if(StringUtils.isBlank(this.createdDateTime)) {
			this.createdDateTime = LocalDateTime.now().toString();
		}
		this.createdDateTime = createdDateTime;
	}
	public String getUpdatedDateTime() {
		return updatedDateTime;
	}
	
	public void setUpdatedDateTime(String updatedDateTime) {
		this.updatedDateTime = LocalDateTime.now().toString();
	}
	
	
}
