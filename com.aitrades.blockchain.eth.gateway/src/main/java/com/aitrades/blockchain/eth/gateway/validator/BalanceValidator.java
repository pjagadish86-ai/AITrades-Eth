package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

@Component
public class BalanceValidator {

	private static final String BALANCE_NOT_GOOD = "Insuffcient Balance you fucker!!!";
	
	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;
	
	public RestExceptionMessage validateBalance(Order order) {
		if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.SELL.name()) && StringUtils.isBlank(order.getParentSnipeId())) {
			boolean hasValidBal = hasValidBalance(order);
			if(!hasValidBal) {
				return new RestExceptionMessage(order.getId(), BALANCE_NOT_GOOD);
			}
		}
//		boolean nativeCoinBalance = orderProcessorPrechecker.getNativeCoinBalance(order.getPublicKey(), order.getFrom().getAmountAsBigInteger(), order.getRoute());
//		if(!nativeCoinBalance) {
//			return new RestExceptionMessage(order.getId(), BALANCE_NOT_GOOD);
//		}
		return null;
	}
	
	public RestExceptionMessage validateSnipeBalance(SnipeTransactionRequest snipeTransactionRequest) {
		boolean nativeCoinBalance = orderProcessorPrechecker.getNativeCoinBalance(snipeTransactionRequest.getPublicKey(), snipeTransactionRequest.getInputTokenValueAmountAsBigInteger(), snipeTransactionRequest.getRoute());
		if(nativeCoinBalance) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
		}
		return null;
	}
	
	
	private boolean hasValidBalance(Order order) {
		try {
			return orderProcessorPrechecker.getBalance(order);
		} catch (Exception e) {
		}
		return false;
	}
	
	//String id, BigDecimal inputAmount, String publicKey, String address, String route, String decimals
	public RestExceptionMessage validateSnipeTokenBalance(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		boolean tokenBalance = orderProcessorPrechecker.getBalance(snipeTransactionRequest.getId(), 
				snipeTransactionRequest.getInputTokenValueAmountAsBigDecimal(), 
				snipeTransactionRequest.getPublicKey(), 
				snipeTransactionRequest.getToAddress(),
				snipeTransactionRequest.getRoute(),
				snipeTransactionRequest.getToAddressDecimals());
		 
		 if(tokenBalance) {
				return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
			}
		return null;
	}
}
