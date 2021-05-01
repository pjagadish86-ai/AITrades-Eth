package com.aitrades.blockchain.eth.gateway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.DexContractStaticCodeValue;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.repository.DexContractStaticCodeValueRepository;
import com.github.benmanes.caffeine.cache.Caffeine;

@Service
public class DexContractStaticCodeValuesService {
	

	private static com.github.benmanes.caffeine.cache.Cache<String, Map<String, String>> staticCodeValues;

	@Autowired
	private DexContractStaticCodeValueRepository dexContractStaticCodeValueRepository;
	
	@Autowired
    private DexContractStaticCodeValuesService() {
        staticCodeValues = Caffeine.newBuilder()
	                               .expireAfterWrite(3, TimeUnit.HOURS)
	                               .build();
    }
	
	
	public String getDexContractAddress(String route, String type) {
		getDexContractAddress(route);
		return staticCodeValues.getIfPresent(route) != null ? staticCodeValues.getIfPresent(route).get(type) : null;
	}
	
	private Map<String, String> getDexContractAddress(String route){
        return staticCodeValues.get(route, this :: getStaticCodeValuesMap);
	}
	
	private Map<String, String> getStaticCodeValuesMap(String route) {
		List<DexContractStaticCodeValue> staticCodeValues = dexContractStaticCodeValueRepository.fetchAllDexContractRouterAndFactoryAddress();
		Map<String, String> contractAddress = new HashMap<>();	
		for(DexContractStaticCodeValue dexContractStaticCodeValue : staticCodeValues) {
			if(StringUtils.equalsIgnoreCase(route, dexContractStaticCodeValue.getDexName())) {
				contractAddress.put(TradeConstants.ROUTER, dexContractStaticCodeValue.getRouterAddress());
				contractAddress.put(TradeConstants.FACTORY, dexContractStaticCodeValue.getFactoryAddress());
				contractAddress.put(TradeConstants.WNATIVE, dexContractStaticCodeValue.getWrappedNativeAddress());
				return contractAddress;
			}
		}
		return null;
	}


	public void insert(DexContractStaticCodeValue dexContractStaticCodeValue) {
		dexContractStaticCodeValueRepository.insert(dexContractStaticCodeValue);
	}
}
