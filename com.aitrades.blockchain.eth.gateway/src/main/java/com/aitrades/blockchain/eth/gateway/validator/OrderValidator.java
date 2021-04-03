package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.Order;
@Component
public class OrderValidator {

	private static final Set<String> GAS_MODES = Set.of("ULTRA", "FASTEST", "FAST", "STANDARD", "SAFELOW", "CUSTOM");
	
	private static final Set<String> ORDER_SIDE = Set.of("BUY", "SELL");
	
	private static final Set<String> ORDER_TYPE = Set.of("SNIPE", "MARKET", "LIMIT", "STOPLOSS", "STOPLIMIT", "TRAILLING_STOP", "LIMIT_TRAILLING_STOP");

	private static final Set<String> ORDER_STATE = Set.of("FILLED", "PARTIAL_FILLED", "WORKING", "CANCELLED");

	private static final Set<String> ROUTES = Set.of("UNISWAP", "SUSHI", "PANCAKE");
	
	
	public RestExceptionMessage validatorOrder(Order order) {
		
		if(order.getFrom() == null 
				|| order.getFrom().getTicker() == null 
				|| StringUtils.isBlank(order.getFrom().getTicker().getAddress())) {
			return new RestExceptionMessage(order.getId(), "From Ticker Entity is Empty");
		}
		
		if(order.getTo() == null 
				|| order.getTo().getTicker() == null 
				|| StringUtils.isBlank(order.getTo().getTicker().getAddress())) {
			return new RestExceptionMessage(order.getId(), "To Ticker Entity is Empty");
		}
		
		
		if(StringUtils.isBlank(order.getFrom().getAmount()) 
				|| order.getFrom().getAmountAsBigInteger() == null
				|| order.getFrom().getAmountAsBigInteger().compareTo(BigInteger.ZERO) <= 0) {
			return new RestExceptionMessage(order.getId(), "Invalid input amount ");
		}
		
		
		if(order.getSlippage() == null 
				|| StringUtils.isEmpty(order.getSlippage().getSlipagePercent())
				|| order.getSlippage().getSlipageInBipsInDouble() <= 0) {
			return new RestExceptionMessage(order.getId(), "Slipage is invalid");
		}
		
		if(order.getSlippage() == null 
				|| StringUtils.isEmpty(order.getSlippage().getSlipagePercent())
				|| order.getSlippage().getSlipageInBipsInDouble() <= 0) {
			return new RestExceptionMessage(order.getId(), "Slipage is invalid");
		}
		
		if(StringUtils.isEmpty(order.getGasMode()) 
				|| !GAS_MODES.contains(order.getGasMode())) {
			return new RestExceptionMessage(order.getId(), "Invalid gas mode");
		}
		
		if(StringUtils.isNotBlank(order.getGasMode()) 
				&& (order.getGasLimit() == null 
				|| StringUtils.isBlank(order.getGasPrice().getValue() ) 
				|| order.getGasPrice().getValueBigDecimal().compareTo(BigDecimal.ZERO) <= 0 
				|| order.getGasPrice().getValueBigInteger().compareTo(BigInteger.ZERO) <= 0)) {
			return new RestExceptionMessage(order.getId(), "Invalid gas Gas Price Amount");
		}

		if(StringUtils.isNotBlank(order.getGasMode()) 
				&& (order.getGasLimit() == null 
						|| StringUtils.isBlank(order.getGasLimit().getValue() ) 
						|| order.getGasLimit().getValueBigDecimal().compareTo(BigDecimal.ZERO) <= 0 
						|| order.getGasLimit().getValueBigInteger().compareTo(BigInteger.ZERO) <= 0)) {
			return new RestExceptionMessage(order.getId(), "Invalid gas Gas Limit Amount");
		}
		
		if(order.getOrderEntity() != null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderSide() )
				|| (!ORDER_SIDE.contains(order.getOrderEntity().getOrderSide() ))) {
			return new RestExceptionMessage(order.getId(), "Invalid Order Side");
		}
		
		if(order.getOrderEntity() != null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderType() )
				|| (!ORDER_TYPE.contains(order.getOrderEntity().getOrderType() ))) {
			return new RestExceptionMessage(order.getId(), "Invalid Order Type");
		}
		
		if(order.getOrderEntity() != null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderType() )
				|| (!ORDER_STATE.contains(order.getOrderEntity().getOrderType() ))) {
			return new RestExceptionMessage(order.getId(), "Invalid Order State");
		}
		
		if(StringUtils.isEmpty(order.getRoute()) 
				|| !ROUTES.contains(order.getRoute())) {
			return new RestExceptionMessage(order.getId(), "Invalid Route mode");
		}
		
		// Limit Order 
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , "LIMIT")) {
			if(order.getOrderEntity().getLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitOrder().getLimitPrice())
					|| order.getOrderEntity().getLimitOrder().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid Limit order params.");
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , "STOPLOSS")) {
			if(order.getOrderEntity().getStopOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopOrder().getStopPrice())
					|| order.getOrderEntity().getStopOrder().getStopPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid Stop Order params.");
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , "STOPLIMIT")) {
			
			if(order.getOrderEntity().getStopLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopLimitOrder().getLimitPrice())
					|| order.getOrderEntity().getStopLimitOrder().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid StopLimit limit order params.");
			}
			
			
			if(order.getOrderEntity().getStopLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopLimitOrder().getStopPrice())
					|| order.getOrderEntity().getStopLimitOrder().getStopPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid StopLimit Stop Order params.");
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , "TRAILLING_STOP")) {
			if(order.getOrderEntity().getTrailingStopOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getTrailingStopOrder().getTrailingStopPercent())
					|| order.getOrderEntity().getTrailingStopOrder().getTrailingStopPercentBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid trailing stop params.");
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , "LIMIT_TRAILLING_STOP")) {
			if(order.getOrderEntity().getLimitTrailingStop() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitTrailingStop().getTrailingStopPercent())
					|| order.getOrderEntity().getLimitTrailingStop().getTrailingStopPercentBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid limit trailling trailing order params.");
			}
			
			if(order.getOrderEntity().getLimitTrailingStop() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitTrailingStop().getLimitPrice())
					|| order.getOrderEntity().getLimitTrailingStop().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), "Invalid limit trailling trailing order params.");
			}
		}
		
		return null;
		
	}
}
