package com.aitrades.blockchain.eth.gateway.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderType;
import com.aitrades.blockchain.eth.gateway.domain.StopLimitOrder;
import com.aitrades.blockchain.eth.gateway.domain.TradeOverview;
import com.aitrades.blockchain.eth.gateway.price.Cryptonator;
import com.aitrades.blockchain.eth.gateway.price.PriceClient;
import com.aitrades.blockchain.eth.gateway.web3j.EthereumDexContract;

@Service
@SuppressWarnings("rawtypes")
public class TakeProfitOrderStrategy {

	@Autowired
	private EthereumDexContract ethereumDexContract;

	@Autowired
	private PriceClient cryptonatorNativeCoinPrice;
	
	
	public Order buildSellOrderStrategy(Order order, TradeOverview tradeOverview) throws Exception {
		String balance = null;
		BigDecimal balanceAsBigDec = null;
		BigInteger balanceAsBigInt = null;
		List<Type> types  = ethereumDexContract.getBalance(order.getWalletInfo().getPublicKey(), order.getFrom().getTicker().getAddress(), order.getRoute());
		if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
			for(Type type : types) {
				balanceAsBigInt = (BigInteger)type.getValue();
				balance = Convert.fromWei(balanceAsBigInt.toString(), Convert.Unit.ETHER).toString();
				balanceAsBigDec = new BigDecimal(balance);
			}
			
			order.getFrom().setAmount(balance);
			order.getFrom().setAmountAsBigDecimal(balanceAsBigDec);
			order.getFrom().setAmountAsBigInteger(balanceAsBigInt);
			order.getOrderEntity().setLimitTrailingStop(null);
			order.getOrderEntity().setLimitOrder(null);
			order.getOrderEntity().setStopOrder(null);
			order.getOrderEntity().setTrailingStopOrder(null);
			order.getOrderEntity().setOrderType(OrderType.STOPLIMIT.name());
			order.getOrderEntity().setStopLimitOrder(buildStopLimitOrder(order, tradeOverview, order.getCredentials()));
		}
		return order;
	}


	private StopLimitOrder buildStopLimitOrder(Order order, TradeOverview tradeOverview, Credentials credentials) throws Exception {
	
		final BigDecimal currentPriceOfTicker  = cryptonatorNativeCoinPrice.tokenPrice(order.getPairData().getPairAddress().getAddress(), order.getRoute(), credentials);
		
		BigDecimal adjustedLimitPrice = currentPriceOfTicker.subtract(currentPriceOfTicker.multiply(new BigDecimal(order.getAutoSnipeLimitSellTrailPercent()).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN)));

		final BigDecimal adjustedStopPrice = currentPriceOfTicker.subtract(currentPriceOfTicker.multiply(tradeOverview.getExecutedPrice()).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN));

		if(adjustedLimitPrice.compareTo(adjustedStopPrice) <=0 ) {
			adjustedLimitPrice = adjustedStopPrice.multiply(BigDecimal.valueOf(1.5));
		}
		
		StopLimitOrder stopLimitOrder = new StopLimitOrder();
		stopLimitOrder.setLimitPrice(adjustedLimitPrice.toString());
		stopLimitOrder.setLimitPriceBigDecimal(adjustedLimitPrice);
		stopLimitOrder.setLimitPriceBigInteger(adjustedLimitPrice.toBigInteger());
		
		stopLimitOrder.setStopPrice(adjustedStopPrice.toString());
		stopLimitOrder.setStopPriceBigDecimal(adjustedStopPrice);
		stopLimitOrder.setStopPriceBigInteger(BigInteger.valueOf(adjustedStopPrice.longValue()));
		return stopLimitOrder;
	}

}
