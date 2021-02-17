package com.aitrades.blockchain.gateway.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class AuthenicatorOTP {

	private String smsPin;
	private String googleAuthPin;
	private String emailAuthPin;
	
	@JsonCreator
	public AuthenicatorOTP(@JsonProperty("smsPin") String smsPin,
						   @JsonProperty("googleAuthPin") String googleAuthPin, 
						   @JsonProperty("emailAuthPin")  String emailAuthPin) {
		this.smsPin = smsPin;
		this.googleAuthPin = googleAuthPin;
		this.emailAuthPin = emailAuthPin;
	}

	public String getSmsPin() {
		return smsPin;
	}

	public String getGoogleAuthPin() {
		return googleAuthPin;
	}

	public String getEmailAuthPin() {
		return emailAuthPin;
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
