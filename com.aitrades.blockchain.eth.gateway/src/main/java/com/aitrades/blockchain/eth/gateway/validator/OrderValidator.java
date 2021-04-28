package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.Order;
@Component
public class OrderValidator {

	@Autowired
	private BalanceValidator balanceValidator;
	
	private static final String CUSTOM = "CUSTOM";
	private static final String INVALID_ORDER_SIDE = "Invalid Order Side";
	private static final String LIMIT = "LIMIT";
	private static final String STOPLOSS = "STOPLOSS";
	private static final String STOPLIMIT = "STOPLIMIT";
	private static final String TRAILLING_STOP = "TRAILLING_STOP";
	private static final String INVALID_LIMIT_TRAILLING_TRAILING_ORDER_PARAMS = "Invalid limit trailling trailing order params.";
	private static final String LIMIT_TRAILLING_STOP = "LIMIT_TRAILLING_STOP";
	private static final String INVALID_TRAILING_STOP_PARAMS = "Invalid trailing stop params.";
	private static final String INVALID_STOP_LIMIT_STOP_ORDER_PARAMS = "Invalid StopLimit Stop Order params.";
	private static final String INVALID_STOP_LIMIT_LIMIT_ORDER_PARAMS = "Invalid StopLimit limit order params.";
	private static final String INVALID_STOP_ORDER_PARAMS = "Invalid Stop Order params.";
	private static final String INVALID_LIMIT_ORDER_PARAMS = "Invalid Limit order params.";
	private static final String INVALID_ROUTE_MODE = "Invalid Route mode";
	private static final String INVALID_ORDER_STATE = "Invalid Order State";
	private static final String INVALID_ORDER_TYPE = "Invalid Order Type";
	private static final String INVALID_GAS_GAS_LIMIT_AMOUNT = "Invalid gas Gas Limit Amount";
	private static final String INVALID_GAS_GAS_PRICE_AMOUNT = "Invalid gas Gas Price Amount";
	private static final String INVALID_GAS_MODE = "Invalid gas mode";
	private static final String SLIPAGE_IS_INVALID = "Slipage is invalid";
	private static final String INVALID_INPUT_AMOUNT = "Invalid input amount ";
	private static final String TO_TICKER_ENTITY_IS_EMPTY = "To Ticker Entity is Empty";
	private static final String FROM_TICKER_ENTITY_IS_EMPTY = "From Ticker Entity is Empty";

	private static final Set<String> GAS_MODES = Set.of("ULTRA", "FASTEST", "FAST", "STANDARD", "SAFELOW", CUSTOM);
	
	private static final Set<String> ORDER_SIDE = Set.of("BUY", "SELL");
	
	private static final Set<String> ORDER_TYPE = Set.of("SNIPE", "MARKET", LIMIT, STOPLOSS, STOPLIMIT, TRAILLING_STOP, LIMIT_TRAILLING_STOP);

	private static final Set<String> ORDER_STATE = Set.of("FILLED", "PARTIAL_FILLED", "WORKING", "CANCELLED");

	private static final Set<String> ROUTES = Set.of("UNISWAP", "SUSHI", "PANCAKE");
	
	// we should check balance for buy and sell and skip only when we have parentid not null
	public RestExceptionMessage validatorOrder(Order order) throws Exception {
		
		if(order.getFrom() == null 
				|| order.getFrom().getTicker() == null 
				|| StringUtils.isBlank(order.getFrom().getTicker().getAddress())) {
			return new RestExceptionMessage(order.getId(), FROM_TICKER_ENTITY_IS_EMPTY);
		}
		
		if(order.getTo() == null 
				|| order.getTo().getTicker() == null 
				|| StringUtils.isBlank(order.getTo().getTicker().getAddress())) {
			return new RestExceptionMessage(order.getId(), TO_TICKER_ENTITY_IS_EMPTY);
		}
		
		if(StringUtils.isBlank(order.getParentSnipeId())) {
			if(StringUtils.isBlank(order.getFrom().getAmount()) 
					|| order.getFrom().getAmountAsBigInteger() == null
					|| order.getFrom().getAmountAsBigInteger().compareTo(BigInteger.ZERO) <= 0) {
				return new RestExceptionMessage(order.getId(), INVALID_INPUT_AMOUNT);
			}
		}
		
		if(order.getSlippage() == null 
				|| StringUtils.isEmpty(order.getSlippage().getSlipagePercent())
				|| order.getSlippage().getSlipageInBipsInDouble() <= 0) {
			return new RestExceptionMessage(order.getId(), SLIPAGE_IS_INVALID);
		}
		
		if(StringUtils.isEmpty(order.getGasMode()) 
				|| !GAS_MODES.contains(order.getGasMode())) {
			return new RestExceptionMessage(order.getId(), INVALID_GAS_MODE);
		}
		
		if(StringUtils.isNotBlank(order.getGasMode()) 
				&& StringUtils.equalsIgnoreCase(order.getGasMode(), CUSTOM) 
				&& StringUtils.isBlank(order.getGasPrice().getValue() ) 
				&& order.getGasPrice().getValueBigDecimal().compareTo(BigDecimal.ZERO) <= 0 
				&& order.getGasPrice().getValueBigInteger().compareTo(BigInteger.ZERO) <= 0) {
			return new RestExceptionMessage(order.getId(), INVALID_GAS_GAS_PRICE_AMOUNT);
		}
		
		if(StringUtils.isNotBlank(order.getGasMode()) 
				&& StringUtils.equalsIgnoreCase(order.getGasMode(), CUSTOM) 
				&& StringUtils.isBlank(order.getGasLimit().getValue() ) 
				&& order.getGasLimit().getValueBigDecimal().compareTo(BigDecimal.ZERO) <= 0 
				&& order.getGasLimit().getValueBigInteger().compareTo(BigInteger.ZERO) <= 0) {
			return new RestExceptionMessage(order.getId(), INVALID_GAS_GAS_LIMIT_AMOUNT);
		}

		if(order.getOrderEntity() == null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderSide())
				|| !ORDER_SIDE.contains(order.getOrderEntity().getOrderSide())) {
			return new RestExceptionMessage(order.getId(), INVALID_ORDER_SIDE);
		}
		
		if(order.getOrderEntity() == null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderType() )
				|| !ORDER_TYPE.contains(order.getOrderEntity().getOrderType())) {
			return new RestExceptionMessage(order.getId(), INVALID_ORDER_TYPE);
		}
		
		if(order.getOrderEntity() == null 
				|| StringUtils.isBlank(order.getOrderEntity().getOrderState())
				|| !ORDER_STATE.contains(order.getOrderEntity().getOrderState())) {
			return new RestExceptionMessage(order.getId(), INVALID_ORDER_STATE);
		}
		
		if(StringUtils.isEmpty(order.getRoute()) 
				|| !ROUTES.contains(order.getRoute())) {
			return new RestExceptionMessage(order.getId(), INVALID_ROUTE_MODE);
		}
		
		// Limit Order 
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , LIMIT)) {
			if(order.getOrderEntity().getLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitOrder().getLimitPrice())
					|| order.getOrderEntity().getLimitOrder().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_LIMIT_ORDER_PARAMS);
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , STOPLOSS)) {
			if(order.getOrderEntity().getStopOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopOrder().getStopPrice())
					|| order.getOrderEntity().getStopOrder().getStopPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_STOP_ORDER_PARAMS);
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , STOPLIMIT)) {
			
			if(order.getOrderEntity().getStopLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopLimitOrder().getLimitPrice())
					|| order.getOrderEntity().getStopLimitOrder().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_STOP_LIMIT_LIMIT_ORDER_PARAMS);
			}
			
			
			if(order.getOrderEntity().getStopLimitOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getStopLimitOrder().getStopPrice())
					|| order.getOrderEntity().getStopLimitOrder().getStopPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_STOP_LIMIT_STOP_ORDER_PARAMS);
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , TRAILLING_STOP)) {
			if(order.getOrderEntity().getTrailingStopOrder() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getTrailingStopOrder().getTrailingStopPercent())
					|| order.getOrderEntity().getTrailingStopOrder().getTrailingStopPercentBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_TRAILING_STOP_PARAMS);
			}
		}
		
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderType() , LIMIT_TRAILLING_STOP)) {
			if(order.getOrderEntity().getLimitTrailingStop() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitTrailingStop().getTrailingStopPercent())
					|| order.getOrderEntity().getLimitTrailingStop().getTrailingStopPercentBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_LIMIT_TRAILLING_TRAILING_ORDER_PARAMS);
			}
			
			if(order.getOrderEntity().getLimitTrailingStop() == null 
					|| StringUtils.isBlank(order.getOrderEntity().getLimitTrailingStop().getLimitPrice())
					|| order.getOrderEntity().getLimitTrailingStop().getLimitPriceBigDecimal().compareTo(BigDecimal.ZERO) <= 0 ) {
				return new RestExceptionMessage(order.getId(), INVALID_LIMIT_TRAILLING_TRAILING_ORDER_PARAMS);
			}
		}
		
		return balanceValidator.validateBalance(order);
		
	}
}
