package com.aitrades.blockchain.eth.gateway.price;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.tuples.generated.Tuple3;

import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
import com.aitrades.blockchain.eth.gateway.web3j.EthereumDexContractReserves;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableMap;

import io.reactivex.schedulers.Schedulers;

@Service
//TODO: price url should be coming from mongodb.
public class PriceClient {
	
	private static com.github.benmanes.caffeine.cache.Cache<String, Cryptonator> tokenCache;   
	
	private static final String BNB_USD_PRICE = "https://api.cryptonator.com/api/ticker/bnb-usd";
	
	private static final String ETH_USD_PRICE = "https://api.cryptonator.com/api/ticker/eth-usd";
	
	private static final String FTM_USD_PRICE = "https://api.cryptonator.com/api/ticker/ftm-usd";
	
	private static final Map<String, String> BLOCKCHAIN_NATIVE_PRICE_ORACLE = ImmutableMap.of(TradeConstants.UNISWAP, ETH_USD_PRICE, 
			TradeConstants.SUSHI, ETH_USD_PRICE, 
			TradeConstants.PANCAKE, BNB_USD_PRICE,
			TradeConstants.FTM, FTM_USD_PRICE);

	
	@Resource(name="cryptonatorObjectReader")
	private ObjectReader cryptonatorObjectReader;
	
	@Autowired
	private Web3jServiceClientFactory web3jServiceClientFactory;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Cryptonator nativeCoinPrice(String route) throws Exception {
		return web3jServiceClientFactory.getWeb3jMap().get(route).getRestTemplate().getForEntity(BLOCKCHAIN_NATIVE_PRICE_ORACLE.get(route), Cryptonator.class).getBody();
	}
	
	@Autowired
    private PriceClient() {
        tokenCache = Caffeine.newBuilder()
                             .expireAfterWrite(1, TimeUnit.MINUTES)
                             .build();
    }

	private Cryptonator getNtvPrice(String route){
        return tokenCache.get(route, rout -> {
			try {
				return this.nativeCoinPrice(rout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
    }

	private String getPrice(String route) throws Exception  {
        return getNtvPrice(route).getTicker().getPrice();
    }
	
	private Tuple3<BigInteger, BigInteger, BigInteger> getReserves(String pairAddress, String route,  Credentials credentials) {
		EthereumDexContractReserves ethereumDexContractReserves = new EthereumDexContractReserves(pairAddress, web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j(), credentials);
		return ethereumDexContractReserves.getReserves().flowable().subscribeOn(Schedulers.io()).blockingSingle();
	}

	public BigDecimal tokenPrice(String pairAddress, String route, Credentials credentials) throws Exception {
		Tuple3<BigInteger, BigInteger, BigInteger> reserves  =  getReserves(pairAddress, route, credentials);
		if(reserves == null) {
			return null;
		}
		String price = getPrice(route);
		if(StringUtils.isBlank(price)) {
			return null;
		}
		BigInteger divide = reserves.component1().divide(reserves.component2());
		Double priceOFToken = (Double.valueOf(1)/ divide.doubleValue()) * Double.valueOf(price);
		logger.info("Price of token for pairAddress ={}, route={}, price={}, reserves={}", pairAddress, route, priceOFToken, reserves);
		try {
			return BigDecimal.valueOf(priceOFToken);
		} catch (Exception e) {
			logger.info("exeception orccured reversing the reserves for price calculation of token for pairAddress ={}, route={}, price={}, reserves={}", pairAddress, route, priceOFToken, reserves);
			BigInteger divide1 = reserves.component2().divide(reserves.component1());
			Double priceOFToken1 = (Double.valueOf(1)/ divide1.doubleValue()) * Double.valueOf(price);
			return BigDecimal.valueOf(priceOFToken1);
		}
	}


}
