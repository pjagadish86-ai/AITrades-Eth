/**
 * 
 */
package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.BlockchainExchange;
import com.aitrades.blockchain.eth.gateway.domain.DexContractStaticCodeValue;
import com.aitrades.blockchain.eth.gateway.domain.EndpointConfig;
import com.aitrades.blockchain.eth.gateway.repository.DexContractStaticCodeValueRepository;
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
	
	@Autowired
	private DexContractStaticCodeValueRepository dexContractStaticCodeValueRepository;
	
	@PostMapping("/config")
	public void createConfig() {
	
		BlockchainExchange blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("ETH");
		blockchainExchange.setExchangeName("UNISWAPV2");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(1);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		// router, factory, wrapped native, usdc 
		DexContractStaticCodeValue dexContractStaticCodeValue = new DexContractStaticCodeValue();
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("ETH");
		dexContractStaticCodeValue.setDexName("UNISWAPV2");
		dexContractStaticCodeValue.setCode(1);
		dexContractStaticCodeValue.setFactoryAddress("0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f");
		dexContractStaticCodeValue.setRouterAddress("0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("ETH");
		blockchainExchange.setExchangeName("SUSHIV2");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(2);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("ETH");
		dexContractStaticCodeValue.setDexName("SUSHIV2");
		dexContractStaticCodeValue.setCode(2);
		dexContractStaticCodeValue.setFactoryAddress("0xc0aee478e3658e2610c5f7a4a2e1777ce9e4f2ac");
		dexContractStaticCodeValue.setRouterAddress("0xd9e1cE17f2641f24aE83637ab66a2cca9C378B9F");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("BSC");
		blockchainExchange.setExchangeName("PANCAKEV2");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(3);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("BSC");
		dexContractStaticCodeValue.setDexName("PANCAKEV2");
		dexContractStaticCodeValue.setCode(3);
		dexContractStaticCodeValue.setFactoryAddress("0xca143ce32fe78f1f7019d7d551a6402fc5350c73");
		dexContractStaticCodeValue.setRouterAddress("0x10ed43c718714eb63d5aa57b78b54704e256024e");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("FTM");
		blockchainExchange.setExchangeName("SPIRIT");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(4);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("FTM");
		dexContractStaticCodeValue.setDexName("SPIRIT");
		dexContractStaticCodeValue.setCode(4);
		dexContractStaticCodeValue.setFactoryAddress("0xef45d134b73241eda7703fa787148d9c9f4950b0");
		dexContractStaticCodeValue.setRouterAddress("0x16327E3FbDaCA3bcF7E38F5Af2599D2DDc33aE52");
		dexContractStaticCodeValue.setWrappedNativeAddress("0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("FTM");
		blockchainExchange.setExchangeName("SPOOKY");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(5);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("FTM");
		dexContractStaticCodeValue.setDexName("SPOOKY");
		dexContractStaticCodeValue.setCode(5);
		dexContractStaticCodeValue.setFactoryAddress("1");
		dexContractStaticCodeValue.setRouterAddress("1");
		dexContractStaticCodeValue.setWrappedNativeAddress("0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("FTM");
		blockchainExchange.setExchangeName("HYPERJUMP");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(6);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("FTM");
		dexContractStaticCodeValue.setDexName("HYPERJUMP");
		dexContractStaticCodeValue.setCode(6);
		dexContractStaticCodeValue.setFactoryAddress("0x991152411a7b5a14a8cf0cdde8439435328070df");
		dexContractStaticCodeValue.setRouterAddress("0x53c153a0df7E050BbEFbb70eE9632061f12795fB");
		dexContractStaticCodeValue.setWrappedNativeAddress("0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("FTM");
		blockchainExchange.setExchangeName("SUSHI");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(7);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("FTM");
		dexContractStaticCodeValue.setDexName("SUSHI");
		dexContractStaticCodeValue.setCode(7);
		dexContractStaticCodeValue.setFactoryAddress("0xc35dadb65012ec5796536bd9864ed8773abc74c4");
		dexContractStaticCodeValue.setRouterAddress("0x1b02dA8Cb0d097eB8D57A175b88c7D8b47997506");
		dexContractStaticCodeValue.setWrappedNativeAddress("0x21be370D5312f44cB42ce377BC9b8a0cEF1A4C83");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("MATIC");
		blockchainExchange.setExchangeName("QUICKSWAP");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(8);
		blockchainExchange.setVersion("V2");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("FTM");
		dexContractStaticCodeValue.setDexName("SUSHI");
		dexContractStaticCodeValue.setCode(8);
		dexContractStaticCodeValue.setFactoryAddress("0x5757371414417b8c6caad45baef941abc7d3ab32");
		dexContractStaticCodeValue.setRouterAddress("0xa5E0829CaCEd8fFDD4De3c43696c57F7D7A678ff");
		dexContractStaticCodeValue.setWrappedNativeAddress("0x7ceb23fd6bc0add59e62ac25578270cff1b9f619");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("ETH");
		blockchainExchange.setExchangeName("UNISWAPV3");
		blockchainExchange.setEnabled(true);
		blockchainExchange.setCode(9);
		blockchainExchange.setVersion("V3");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		dexContractStaticCodeValue.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		dexContractStaticCodeValue.setDexChain("ETH");
		dexContractStaticCodeValue.setDexName("UNISWAPV3");
		blockchainExchange.setCode(9);
		dexContractStaticCodeValue.setFactoryAddress("0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f");
		dexContractStaticCodeValue.setRouterAddress("0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D");
		dexContractStaticCodeValue.setWrappedNativeAddress("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		codeValuesService.insert(dexContractStaticCodeValue);
		
		EndpointConfig endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("ETH");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setEnabled(true);
		endpointConfig.setExplorerUri("https://etherscan.io/tx/");
		endpointConfig.setEndpointUrl("wss://cool-sparkling-dawn.quiknode.pro/d5ffadd5dde5cbfe5e2c1d919316cf4ab383858d/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
		endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("BSC");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setEnabled(true);
		endpointConfig.setExplorerUri("https://bscscan.com/tx/");
		endpointConfig.setEndpointUrl("wss://holy-twilight-violet.bsc.quiknode.pro/9ccdc8c6748f92a972bc9c9c1b8b56de961c0fc6/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
		endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("FTM");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setEnabled(true);
		endpointConfig.setExplorerUri("https://ftmscan.com/tx/");
		endpointConfig.setEndpointUrl("wss://wsapi.fantom.network/");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
		endpointConfig = new EndpointConfig();
		endpointConfig.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		endpointConfig.setBlockchain("MATIC");
		endpointConfig.setUsername("AITRADES");
		endpointConfig.setEnabled(true);
		endpointConfig.setExplorerUri("https://explorer-mainnet.maticvigil.com/tx/0x0fd2c2d48229d03ca17bf256751d7c1ff4582f17b23df3e3d4690a16b614aa01/token-transfers");
		endpointConfig.setEndpointUrl("wss://rpc-mainnet.maticvigil.com/ws/v1/ea56d6c86fad27890bdb88279eac92b42be21f0c");
		endpointConfigRepository.addSupportedBlockchainEndpointNodeConfigUrls(endpointConfig);
		
	}
	
}
