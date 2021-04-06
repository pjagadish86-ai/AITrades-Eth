package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;

import reactor.core.scheduler.Schedulers;

@Component
public class StrategyGasProvider{

	private static final String PANCAKE = "PANCAKE";
	private static final String GAS_PRICE_ORACLE ="/gasPriceOracle";

	@Resource(name="gasWebClient")
	private WebClient gasWebClient;
	
	@Autowired
	private Web3jServiceClientFactory  web3jServiceClientFactory;
	
	public static final BigInteger GAS_LIMIT = BigInteger.valueOf(9_000_000);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(4_100_000_000L);
	    
	@SuppressWarnings("unchecked")
	public BigInteger getGasPrice(GasModeEnum gasModeEnum){
		Map<String, Object> gasPrices = gasWebClient.get()
												    .uri(GAS_PRICE_ORACLE)
												    .accept(MediaType.APPLICATION_JSON)
												    .retrieve()
												    .bodyToMono(LinkedCaseInsensitiveMap.class)
												    .subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
												    .block();
		String gasMode = gasModeEnum.getValue().toLowerCase();
		if(StringUtils.equalsIgnoreCase(gasModeEnum.getValue().toLowerCase(), "ULTRA")) {
			gasMode ="FASTEST";
		}
		return Convert.toWei(gasPrices.get(gasMode).toString(), Convert.Unit.GWEI).toBigInteger();
	}
	
	public BigInteger getGasLimit(String route, boolean sensitive){
		return sensitive ? web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
											 .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
											 .flowable()
											 .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
											 .blockingLast()
											 .getBlock()
											 .getGasLimit()
					    : GAS_LIMIT;
	}
	
	@SuppressWarnings("unchecked")
	public BigInteger getGasPricePancake(GasModeEnum gasModeEnum) throws Exception{
		Map<String, Object> gasPrices = gasWebClient.get()
												    .uri(GAS_PRICE_ORACLE)
												    .accept(MediaType.APPLICATION_JSON)
												    .retrieve()
												    .bodyToMono(Map.class)
												    .subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
												    .block();
		String gasMode = gasModeEnum.getValue().toLowerCase();
		if(StringUtils.equalsIgnoreCase(gasModeEnum.getValue().toLowerCase(), "ULTRA")) {
			gasMode ="FASTEST";
		}
		Object gasPrice = gasPrices.get(gasMode);
		if(gasPrice != null) {
			return Convert.toWei(gasPrice.toString(), Convert.Unit.GWEI).toBigInteger();
		}
		return GAS_PRICE;
	}
	
	public BigInteger getGasLimitOfPancake(boolean senstive){
		return senstive ? web3jServiceClientFactory.getWeb3jMap().get(PANCAKE).getWeb3j()
						 .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
						 .flowable()
						 .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
						 .blockingLast()
						 .getBlock()
						 .getGasLimit()
		: GAS_LIMIT;
	}

	public BigInteger getGasLimit(Transaction transaction, String route) {
		return  web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
										 .ethEstimateGas(transaction)
										 .flowable()
										 .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
										 .blockingLast()
										 .getAmountUsed();
	}

}
