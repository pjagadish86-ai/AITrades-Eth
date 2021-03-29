package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

	public void updateLock(Order order) {
		Query query = new Query();
        query.addCriteria(Criteria.where("id").is(order.getId()));
        Update update = new Update();
        update.set("read", "LOCK");
		orderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
	}
	
	public void updateAvail(Order order) {
		Query query = new Query();
        query.addCriteria(Criteria.where("id").is(order.getId()));
        Update update = new Update();
        update.set("read", "AVAL");
     //   update.set("counter", order.getCounter()+1); TODO: Come back
		orderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
	}
}
