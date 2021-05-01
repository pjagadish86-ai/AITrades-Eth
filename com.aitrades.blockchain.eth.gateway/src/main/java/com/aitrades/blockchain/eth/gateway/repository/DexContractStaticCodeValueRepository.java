package com.aitrades.blockchain.eth.gateway.repository;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.BlockchainExchange;
import com.aitrades.blockchain.eth.gateway.domain.DexContractStaticCodeValue;

@Repository
public class DexContractStaticCodeValueRepository {
	private static final String BLOCKCHAIN_EXCHANGES ="blockchainExchanges";

	@Resource(name = "dexCntrStcCdsReactiveMongoTemplate")
	private ReactiveMongoTemplate dexCntrStcCdsReactiveMongoTemplate;
	
	public List<DexContractStaticCodeValue> fetchAllDexContractRouterAndFactoryAddress() {
		return dexCntrStcCdsReactiveMongoTemplate.findAll(DexContractStaticCodeValue.class).collectList().block();
	}

	public void insert(DexContractStaticCodeValue dexContractStaticCodeValue) {
		dexCntrStcCdsReactiveMongoTemplate.insert(dexContractStaticCodeValue).block();
	}
	
	public List<BlockchainExchange> fetchSupportedBlockchains(){
		return dexCntrStcCdsReactiveMongoTemplate.findAll(BlockchainExchange.class, BLOCKCHAIN_EXCHANGES).collectList().block();
		
	}

	public void addBlockchainExchanges(BlockchainExchange blockchainExchange) {
		dexCntrStcCdsReactiveMongoTemplate.insert(blockchainExchange, BLOCKCHAIN_EXCHANGES).block();
	}
}
