package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.TradeOverview;

@Repository
public class TradeOverviewRepository {

	@Resource(name = "tradeOverviewReactiveMongoTemplate")
	private ReactiveMongoTemplate tradeOverviewReactiveMongoTemplate;
	
	@Async
	public void save(TradeOverview tradeOverview) {
		tradeOverviewReactiveMongoTemplate.save(tradeOverview).block();
	}
	
	public TradeOverview findById(String parentSnipeOrderId) {
		return tradeOverviewReactiveMongoTemplate.findById(parentSnipeOrderId, TradeOverview.class).block();
	}
}
