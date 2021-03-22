package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.Order;

import reactor.core.publisher.Mono;

@Repository
public class OrderRepository {

	@Resource(name = "orderReactiveMongoTemplate")
	public ReactiveMongoTemplate orderReactiveMongoTemplate;

	public Mono<Order> insert(Order order) {
		return orderReactiveMongoTemplate.insert(order);
	}
}
