package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.Mono;

@Repository
public class OrderRepository {

	private static final String LOCK = "LOCK";
	private static final String AVAL = "AVAL";
	private static final String READ = "read";
	private static final String ID = "id";
	
	@Resource(name = "orderReactiveMongoTemplate")
	public ReactiveMongoTemplate orderReactiveMongoTemplate;

	public Mono<Order> insert(Order order) {
		return orderReactiveMongoTemplate.insert(order);
	}

	public void updateLock(Order order) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(order.getId()));
        Update update = new Update();
        update.set(READ, LOCK);
		orderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
	}
	
	public void updateAvail(Order order) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(order.getId()));
        Update update = new Update();
        update.set(READ, AVAL);
		orderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
	}

	public void delete(Order order) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(order.getId()));
        DeleteResult deleteResult = orderReactiveMongoTemplate.remove(order).block();
        if(deleteResult != null && deleteResult.getDeletedCount() == 0) {
        	System.out.println("not deleted");
		}else {
			System.out.println("deleted");
		}
	}
}
