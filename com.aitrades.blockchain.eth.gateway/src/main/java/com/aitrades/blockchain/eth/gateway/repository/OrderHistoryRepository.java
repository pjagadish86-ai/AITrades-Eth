package com.aitrades.blockchain.eth.gateway.repository;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.Order;

@Repository
public class OrderHistoryRepository {
	
	private static final String PUBLIC_KEY = "publicKey";
	
	@Resource(name = "orderHistoryReactiveMongoTemplate")
	public ReactiveMongoTemplate orderHistoryReactiveMongoTemplate;

	public Order save(Order order) throws Exception {
		return orderHistoryReactiveMongoTemplate.save(order).block();
	}
	
	public List<Order> fetchOrdersById(List<String> walletIds){
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(walletIds));
		return orderHistoryReactiveMongoTemplate.find(query, Order.class).collectList().block();
	}
}
