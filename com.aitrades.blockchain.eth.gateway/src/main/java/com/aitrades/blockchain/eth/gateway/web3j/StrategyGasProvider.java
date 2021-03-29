package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;

import reactor.core.scheduler.Schedulers;

@Component
//TODO: Gas should be populated in PairCreated Channel and assign it into snipeRequestObject, so this way we can avoid further or do just make a call?
public class StrategyGasProvider implements ContractGasProvider{

	private static final String PANCAKE = "PANCAKE";

	private static final String GAS_PRICE_ORACLE ="/gasPriceOracle";

	@Resource(name="gasWebClient")
	private WebClient gasWebClient;
	
	@Autowired
	public Web3jServiceClientFactory  web3jServiceClientFactory;
	
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(220000000000L); // Gas Price (GWEI) 1
	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(467296);// Gas Limit (Units) 167296
	    
	@SuppressWarnings("unchecked")
	public BigInteger getGasPrice(GasModeEnum gasModeEnum) throws Exception{
		Map<String, Object> gasPrices = gasWebClient.get()
													   .uri(GAS_PRICE_ORACLE)
													   .accept(MediaType.APPLICATION_JSON)
													   .retrieve()
													   .bodyToMono(Map.class)
													   .subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
													   .block();
		return Convert.toWei(gasPrices.get(gasModeEnum.getValue().toLowerCase()).toString(), Convert.Unit.GWEI).toBigInteger();
	}
	
	public BigInteger getGasLimit(String route, boolean sensitive) throws Exception{
		return sensitive ? web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
											 .ethGetBlockByNumber(DefaultBlockParameterName.PENDING, true)
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
		Object gasPrice = gasPrices.get(gasModeEnum.getValue());
		if(gasPrice != null) {
			return Convert.toWei(gasPrice.toString(), Convert.Unit.GWEI).toBigInteger();
		}
		return GAS_PRICE;
		//return Convert.toWei("95", Convert.Unit.GWEI).toBigInteger();
	}
	
	public BigInteger getGasLimitOfPancake(boolean senstive) throws Exception{
		return senstive ? web3jServiceClientFactory.getWeb3jMap().get(PANCAKE).getWeb3j()
						 .ethGetBlockByNumber(DefaultBlockParameterName.PENDING, true)
						 .flowable()
						 .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
						 .blockingLast()
						 .getBlock()
						 .getGasLimit()
		: GAS_LIMIT;
	}

	@Override
	public BigInteger getGasPrice(String route) {
		return getGasLimit(route);
	}

	@Override
	public BigInteger getGasLimit(String contractFunc) {
		return null;
	}

	@Override
	public BigInteger getGasPrice() {
		return new BigInteger("25");
	}

	@Override
	public BigInteger getGasLimit() {
		return new BigInteger("146146");
	}

}
