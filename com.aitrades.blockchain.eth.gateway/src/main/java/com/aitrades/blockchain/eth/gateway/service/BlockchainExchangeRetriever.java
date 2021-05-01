package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.BlockchainExchanges;

@Service
public class BlockchainExchangeRetriever {
	
	@Autowired
	private DexContractStaticCodeValuesService dexContractStaticCodeValuesService;

	public BlockchainExchanges fetchSupportedBlockchainExchanges() throws Exception {
		BlockchainExchanges blockchainExchanges = new BlockchainExchanges();
		blockchainExchanges.setBlockchainExchanges(dexContractStaticCodeValuesService.fetchBlockchainExchanges());
		return blockchainExchanges;
	}
	
}
