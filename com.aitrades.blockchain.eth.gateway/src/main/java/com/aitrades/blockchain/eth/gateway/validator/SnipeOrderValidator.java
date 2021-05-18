package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.RetriggerSnipeOrder;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Component
public class SnipeOrderValidator {
	
	private static final String INVALID_INPUT_AMOUNT = "Invalid Input Amount";
	private static final String INVALID_GAS_GAS_PRICE_AMOUNT = "Invalid gas Gas Price Amount";
	private static final String INVALID_GAS_GAS_LIMIT_AMOUNT = "Invalid gas Gas Limit Amount";
	private static final String INVALID_GAS_MODE = "Invalid gas mode";
	private static final String INVALID_SLIPAGE_AMOUNT = "Invalid Slipage Amount";
	private static final String INVALID_TO_ADDRESS = "Invalid To Address";
	private static final String INVALID_FROM_ADDRESS = "Invalid From Address";
	private static final Set<String> GAS_MODES = Set.of("ULTRA", "FASTEST", "FAST", "STANDARD", "SAFELOW", "CUSTOM");
	private static final String CUSTOM = "CUSTOM";

	@Autowired
	private BalanceValidator balanceValidator;

	public RestExceptionMessage validateSnipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		
		if(StringUtils.isBlank(snipeTransactionRequest.getFromAddress())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_FROM_ADDRESS);
		}
		
		if(StringUtils.isBlank(snipeTransactionRequest.getToAddress())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_TO_ADDRESS);
		}
		
		if(snipeTransactionRequest.getInputTokenValueAmountAsBigInteger() == null 
				|| snipeTransactionRequest.getInputTokenValueAmountAsBigInteger().compareTo(BigInteger.ZERO) <= 0
				|| snipeTransactionRequest.getInputTokenValueAmountAsBigDecimal().compareTo(BigDecimal.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_INPUT_AMOUNT);
		}
		
		if(snipeTransactionRequest.getSlipage() == null
				|| snipeTransactionRequest.getSlipage().compareTo(BigDecimal.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_SLIPAGE_AMOUNT);
		}
		
		if(StringUtils.isEmpty(snipeTransactionRequest.getGasMode())
				|| !GAS_MODES.contains(snipeTransactionRequest.getGasMode())) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_GAS_MODE);
		}
		
		if(StringUtils.isNotBlank(snipeTransactionRequest.getGasMode()) 
				&& StringUtils.equalsIgnoreCase(snipeTransactionRequest.getGasMode(), CUSTOM) 
				&& snipeTransactionRequest.getGasLimit().compareTo(BigInteger.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_GAS_GAS_LIMIT_AMOUNT);
		}
		
		if(StringUtils.isNotBlank(snipeTransactionRequest.getGasMode())
				&& StringUtils.equalsIgnoreCase(snipeTransactionRequest.getGasMode(), CUSTOM) 
				&& snipeTransactionRequest.getGasPrice().compareTo(BigInteger.ZERO) <= 0) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), INVALID_GAS_GAS_PRICE_AMOUNT);
		}

		return null;// balanceValidator.validateSnipeTokenBalance(snipeTransactionRequest);
	}

	public RestExceptionMessage validateRetriggerOrderSnipeOrder(RetriggerSnipeOrder retriggerOrder) {
		if(retriggerOrder == null
				|| retriggerOrder.getParentSnipeOrderId() == null
				|| retriggerOrder.getParentSnipeOrderId().isEmpty()) {
			return new RestExceptionMessage("Invalid retrigger parent id", "Invalid retrigger parent id");
		}
		return null;
	}
	
}
