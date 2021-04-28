package com.aitrades.blockchain.eth.gateway.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

@Component
public class BalanceValidator {

	private static final String BALANCE_NOT_GOOD = "Insuffcient Balance!!!";
	
	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;
	
	public RestExceptionMessage validateBalance(Order order) throws Exception {
		if(StringUtils.isBlank(order.getParentSnipeId())) {// if we have parentsnipeorderid that mean we are auto snipe sell enabled here, so we never have balance here, so we should skip this.
			// for virgin orders we either buy or sell we should be good
			boolean hasValidBal = hasValidBalance(order);
			if(!hasValidBal) {
				return new RestExceptionMessage(order.getId(), BALANCE_NOT_GOOD);
			}
		}
		return null;
	}
	
	public RestExceptionMessage validateNativeCoinBalance(SnipeTransactionRequest snipeTransactionRequest) {
		boolean nativeCoinBalance = orderProcessorPrechecker.getNativeCoinBalance(snipeTransactionRequest.getPublicKey(), snipeTransactionRequest.getInputTokenValueAmountAsBigInteger(), snipeTransactionRequest.getRoute());
		if(!nativeCoinBalance) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
		}
		return null;
	}
	
	
	private boolean hasValidBalance(Order order) {
		try {
			String contractAddress = order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.BUY.name()) ? TradeConstants.WETH_MAP.get(order.getRoute()) : order.getFrom().getTicker().getAddress();
			return orderProcessorPrechecker.getBalanceUsingWrapper(order.getRoute(), order.getFrom().getAmountAsBigInteger(), contractAddress, order.getPublicKey(), order.getCredentials());
		} catch (Exception e) {
		}
		return false;
	}
	
	//String id, BigDecimal inputAmount, String publicKey, String address, String route, String decimals
	public RestExceptionMessage validateSnipeTokenBalance(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		boolean tokenBalance = orderProcessorPrechecker.getBalanceUsingWrapper(snipeTransactionRequest.getRoute(), snipeTransactionRequest.getInputTokenValueAmountAsBigInteger(), TradeConstants.WETH_MAP.get(snipeTransactionRequest.getRoute()), snipeTransactionRequest.getPublicKey(), snipeTransactionRequest.getCredentials());
		if(!tokenBalance) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
		}
		return null;
	}
}
