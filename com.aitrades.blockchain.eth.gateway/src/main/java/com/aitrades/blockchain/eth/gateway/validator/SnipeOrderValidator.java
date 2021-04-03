package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Component
public class SnipeOrderValidator {
	
	private static final Set<String> GAS_MODES = Set.of("ULTRA", "FASTEST", "FAST", "STANDARD", "SAFELOW", "CUSTOM");

	public RestExceptionMessage validateSnipeOrder(SnipeTransactionRequest snipeTransactionRequest) {
		
		if(StringUtils.isBlank(snipeTransactionRequest.getFromAddress())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid From Address");
		}
		
		if(StringUtils.isBlank(snipeTransactionRequest.getToAddress())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid To Address");
		}
		
		if(snipeTransactionRequest.getInputTokenValueAmountAsBigInteger() == null 
				|| snipeTransactionRequest.getInputTokenValueAmountAsBigInteger().compareTo(BigInteger.ZERO) <= 0
				|| snipeTransactionRequest.getInputTokenValueAmountAsBigDecimal().compareTo(BigDecimal.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid Input Amount");
		}
		
		if(snipeTransactionRequest.getSlipage() == null 
				|| snipeTransactionRequest.getSlipage().compareTo(BigDecimal.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid Slipage Amount");
		}
		
		if(StringUtils.isEmpty(snipeTransactionRequest.getGasMode()) 
				|| !GAS_MODES.contains(snipeTransactionRequest.getGasMode())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid gas mode");
		}
		
		if(StringUtils.isNotBlank(snipeTransactionRequest.getGasMode()) 
				&& (snipeTransactionRequest.getGasLimit() == null 
					|| snipeTransactionRequest.getGasLimit().compareTo(BigInteger.ZERO) <= 0)) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid gas Gas Price Amount");
		}

		if(StringUtils.isNotBlank(snipeTransactionRequest.getGasMode()) 
				&& (snipeTransactionRequest.getGasPrice() == null 
					|| snipeTransactionRequest.getGasPrice().compareTo(BigInteger.ZERO) <= 0)) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), "Invalid gas Gas Price Amount");
		}
		
		return null;
	}
	
}
