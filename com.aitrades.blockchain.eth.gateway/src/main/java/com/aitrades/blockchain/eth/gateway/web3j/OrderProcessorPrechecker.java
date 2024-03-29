package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.domain.Ticker;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.DexContractStaticCodeValuesService;
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
	
	@Autowired
	private DexContractStaticCodeValuesService dexContractStaticCodeValuesService;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public Optional<TransactionReceipt> checkTransactionHashSuccess(String transactionHash, String route) throws Exception {
		return web3jServiceClientFactory.getWeb3jMap(route)
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
													  dexContractStaticCodeValuesService.getDexContractAddress(order.getRoute(), TradeConstants.WNATIVE),
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
		String contractAddress = order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.BUY.name()) 
																						?  dexContractStaticCodeValuesService.getDexContractAddress(order.getRoute(), TradeConstants.WNATIVE)
																						: order.getFrom().getTicker().getAddress();
		return getBalanceUsingWrapper(order.getRoute(), order.getFrom().getAmountAsBigInteger(), contractAddress, order.getPublicKey(), order.getCredentials());
	}
	
	@Deprecated
	public boolean getBalance(String id, BigDecimal inputAmount, String publicKey, String address, String route, String decimals) throws Exception {
		List<Type> types  = ethereumDexContract.getBalance(id, publicKey, address, route);
		if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
			BigDecimal balance = Convert.fromWei(types.get(0).getValue().toString(), Convert.Unit.fromString(TradeConstants.DECIMAL_MAP.get(decimals)));
			return inputAmount.compareTo(balance) <= 0;
		}
		return false;
	}
	
	public boolean getNativeCoinBalance(String address, BigInteger inputAmount, String route) throws Exception {
		BigInteger ethOrNativeCoinGetBalance = web3jServiceClientFactory.getWeb3jMap(route).getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).flowable().subscribeOn(Schedulers.io()).blockingSingle().getBalance();
		return inputAmount.compareTo(ethOrNativeCoinGetBalance) <= 0;
	}
	public boolean getBalanceUsingWrapper(String route, BigInteger inputAmount, String contractAddress, String owner, Credentials credentials ) throws Exception {
		BigInteger balance = ethereumDexContract.getBalanceUsingWrappedApi(route, contractAddress, owner, credentials);
		return inputAmount.compareTo(balance) <= 0;
	}
	
	public BigInteger getAccountBalance(String route, String owner) throws Exception {
		 return web3jServiceClientFactory.getWeb3jMap(route)
									     .getWeb3j()
										 .ethGetBalance(owner, DefaultBlockParameterName.LATEST)
										 .flowable()
										 .subscribeOn(Schedulers.io())
										 .blockingSingle()
										 .getBalance();
	}
}
