package com.aitrades.blockchain.gateway.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.gateway.domain.Order;

import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

	Mono<Order> insert(final Order order);
	
}

