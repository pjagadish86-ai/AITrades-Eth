package com.aitrades.blockchain.gateway.domain;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Token implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9126667415972214901L;

	@Id
	private String id;
	private String symbol;
	private String symbolAddress;
	private BigInteger symbolDecimal;
	private String baseSymbol;
	private String baseSymbolAddress;
	private BigInteger baseSymbolDecimal;
	private BigInteger balance;
	private String usdBalance;

	@JsonCreator
	public Token(@JsonProperty("id")  String id,
				 @JsonProperty("symbol")  String symbol, 
				 @JsonProperty("symbolAddress")  String symbolAddress, 
				 @JsonProperty("symbolDecimal")  BigInteger symbolDecimal, 
				 @JsonProperty("baseSymbol")  String baseSymbol, 
				 @JsonProperty("baseSymbolAddress")  String baseSymbolAddress,
				 @JsonProperty("baseSymbolDecimal")  BigInteger baseSymbolDecimal,
				 @JsonProperty("balance")  BigInteger balance) {
		this.id = id;
		this.symbol = symbol;
		this.symbolAddress = symbolAddress;
		this.baseSymbol = baseSymbol;
		this.baseSymbolAddress = baseSymbolAddress;
		this.balance = balance;
		this.baseSymbolDecimal= baseSymbolDecimal;
		this.symbolDecimal = symbolDecimal;
	}

	public Token(@JsonProperty("balance")  BigInteger balance) {
		this.balance = balance;
		this.usdBalance = getUsdBalance();
	}

	public String getId() {
		return id;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getSymbolAddress() {
		return symbolAddress;
	}

	public String getBaseSymbol() {
		return baseSymbol;
	}

	public String getBaseSymbolAddress() {
		return baseSymbolAddress;
	}

	public BigInteger getBalance() {
		return balance;
	}

	public BigInteger getSymbolDecimal() {
		return symbolDecimal;
	}

	public BigInteger getBaseSymbolDecimal() {
		return baseSymbolDecimal;
	}
	
	public String getUsdBalance() {
		return getBalance().divide(new BigInteger("1000000000")).toString();
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
