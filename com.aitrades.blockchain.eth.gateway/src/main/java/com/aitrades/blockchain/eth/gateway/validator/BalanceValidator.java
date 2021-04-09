package com.aitrades.blockchain.eth.gateway.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

@Component
public class BalanceValidator {

	private static final String BALANCE_NOT_GOOD = "Insuffcient Balance!!!";
	
	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;
	
	public RestExceptionMessage validateBalance(Order order) {
		if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.SELL.name()) && StringUtils.isBlank(order.getParentSnipeId())) {
			boolean hasValidBal = hasValidBalance(order);
			if(!hasValidBal) {
				return new RestExceptionMessage(order.getId(), BALANCE_NOT_GOOD);
			}
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
	
}
