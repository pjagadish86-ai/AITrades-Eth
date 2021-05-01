package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.BlockchainExchange;
import com.aitrades.blockchain.eth.gateway.repository.DexContractStaticCodeValueRepository;
import com.aitrades.blockchain.eth.gateway.service.BlockchainExchangeRetriever;

@RestController
@RequestMapping("/exchanges/api/v1")
public class BlockchainExchangeController {
	
	@Autowired
	private BlockchainExchangeRetriever blockchainExchangeRetriever;
	
	
	@Autowired
	private DexContractStaticCodeValueRepository dexContractStaticCodeValueRepository;

	@GetMapping("/exchanges")
	public Object retrieveSupportedBlockchainExchanges() throws Exception {
		return blockchainExchangeRetriever.fetchSupportedBlockchainExchanges();
	}
	
	@GetMapping("/exchanges1")
	public Object addSupportedBlockchainExchanges() throws Exception {
		BlockchainExchange blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("ETH");
		blockchainExchange.setExchangeName("UNISWAP");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("ETH");
		blockchainExchange.setExchangeName("SUSHI");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("BSC");
		blockchainExchange.setExchangeName("PANCAKE");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		blockchainExchange = new BlockchainExchange();
		blockchainExchange.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		blockchainExchange.setBlockchainName("FTM");
		blockchainExchange.setExchangeName("UNKNOWN");
		dexContractStaticCodeValueRepository.addBlockchainExchanges(blockchainExchange);
		
		return "hi"; 
	}
}
