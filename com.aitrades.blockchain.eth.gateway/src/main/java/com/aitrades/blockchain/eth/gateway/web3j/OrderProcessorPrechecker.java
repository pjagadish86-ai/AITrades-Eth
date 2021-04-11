package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.domain.Ticker;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;

import io.reactivex.schedulers.Schedulers;
@SuppressWarnings("rawtypes")
@Component
public class OrderProcessorPrechecker {
	
	private static final String _0X000000 = "0x000000";
	private static final String BUY = "BUY";

	@Autowired
	private Web3jServiceClientFactory  web3jServiceClientFactory;
	
	@Autowired
	private EthereumDexContract ethereumDexContract;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public Optional<TransactionReceipt> checkTransactionHashSuccess(String transactionHash, String route) {
		return web3jServiceClientFactory.getWeb3jMap().get(route)
										.getWeb3j()
									    .ethGetTransactionReceipt(transactionHash)
									    .flowable()
									    .subscribeOn(Schedulers.io())
									    .blockingSingle()
									    .getTransactionReceipt();
	}
	
	public PairData getPairData(Order order) {
		Optional<Type> pairAddress = Optional.empty();
		try {
			pairAddress = ethereumDexContract.getPair(order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY) ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress(), 
											          TradeConstants.WETH_MAP.get(order.getRoute()), 
											          order.getRoute().toUpperCase(),
											          order.getId())
											 .parallelStream()
											 .findFirst();
			logger.info("order id={}, pairaddress ={}", order.getId(), pairAddress);
		} catch (Exception e) {
			logger.error(" exception occured order id={}, pairaddress ={}, exception={}", order.getId(), pairAddress, e);
		}
		if (pairAddress.isPresent()	&& !StringUtils.startsWithIgnoreCase((String) pairAddress.get().getValue(), _0X000000)) {
			PairData pairData = new PairData();
			Ticker ticker = new Ticker();
			ticker.setAddress((String) pairAddress.get().getValue());
			pairData.setPairAddress(ticker);
			return pairData;
		}
		return null;
	}
	
	public boolean getBalance(Order order) throws Exception {
		return getBalance(order.getId(), order.getFrom().getAmountAsBigDecimal(), order.getWalletInfo().getPublicKey(), order.getFrom().getTicker().getAddress(), order.getRoute());
	}
	
	private boolean getBalance(String id, BigDecimal inputAmount, String publicKey, String address, String route) throws Exception {
		List<Type> types  = ethereumDexContract.getBalance(id, publicKey, address, route);
		if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
			BigDecimal balance = Convert.fromWei(types.get(0).getValue().toString(), Convert.Unit.ETHER);
			return inputAmount.compareTo(balance) <= 0;
		}
		return false;
	}

}