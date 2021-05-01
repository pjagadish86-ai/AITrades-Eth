/**
 * 
 */
package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.DexContractStaticCodeValue;
import com.aitrades.blockchain.eth.gateway.domain.EndpointConfig;
import com.aitrades.blockchain.eth.gateway.repository.EndpointConfigRepository;
import com.aitrades.blockchain.eth.gateway.service.DexContractStaticCodeValuesService;

/**
 * @author jay
 *
 */
@RestController
@RequestMapping("/config/api/v1")
public class ConfigController {
	
	@Autowired
	private DexContractStaticCodeValuesService codeValuesService;

	@Autowired
	private EndpointConfigRepository endpointConfigRepository;
	
	@PostMapping("/route")
	public void post(@RequestBody DexContractStaticCodeValue dexContractStaticCodeValue) {
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("ETH");
		dexContractStaticCodeValue.setDexName("UNISWAP");
		dexContractStaticCodeValue.setFactoryAddress("0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f");
		dexContractStaticCodeValue.setRouterAddress("0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("ETH");
		dexContractStaticCodeValue.setDexName("SUSHI");
		dexContractStaticCodeValue.setFactoryAddress("0xc0aee478e3658e2610c5f7a4a2e1777ce9e4f2ac");
		dexContractStaticCodeValue.setRouterAddress("0xd9e1cE17f2641f24aE83637ab66a2cca9C378B9F");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("BSC");
		dexContractStaticCodeValue.setDexName("PANCAKE");
		dexContractStaticCodeValue.setFactoryAddress("0xca143ce32fe78f1f7019d7d551a6402fc5350c73");
		dexContractStaticCodeValue.setRouterAddress("0x10ed43c718714eb63d5aa57b78b54704e256024e");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
	}

	@PostMapping("/endpointConfig")
	public void post() {
		
		EndpointConfig endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("ETH");
		endpointConfig.setExchange("UNISWAP");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setProviderName("QUICKNODE");
		endpointConfig.setEnabled(true);
		endpointConfig.setEndpointUrl("wss://cool-sparkling-dawn.quiknode.pro/d5ffadd5dde5cbfe5e2c1d919316cf4ab383858d/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
		endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("ETH");
		endpointConfig.setExchange("SUSHI");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setProviderName("QUICKNODE");
		endpointConfig.setEnabled(true);
		endpointConfig.setEndpointUrl("wss://cool-sparkling-dawn.quiknode.pro/d5ffadd5dde5cbfe5e2c1d919316cf4ab383858d/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
		endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("BSC");
		endpointConfig.setExchange("PANCAKE");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setProviderName("QUICKNODE");
		endpointConfig.setEnabled(true);
		endpointConfig.setEndpointUrl("wss://holy-twilight-violet.bsc.quiknode.pro/9ccdc8c6748f92a972bc9c9c1b8b56de961c0fc6/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
	}
}
