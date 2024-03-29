package com.aitrades.blockchain.eth.gateway.repository;

import java.time.LocalDateTime;
import java.util.List;

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
	private static final String PUBLIC_KEY = "publicKey";
	
	private static final String APPROVED_HASH = "approvedHash";
	
	@Resource(name = "orderReactiveMongoTemplate")
	private ReactiveMongoTemplate orderReactiveMongoTemplate;

	public Mono<Order> insert(Order order) {
		return orderReactiveMongoTemplate.insert(order);
	}
	
	public Order save(Order order) {
		order.getAuditInformation().setUpdatedDateTime(LocalDateTime.now().toString());
		return orderReactiveMongoTemplate.save(order).block();
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

	public void updateApprovedHash(Order order, String apporvedHash) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(order.getId()));
        Update update = new Update();
        update.set(APPROVED_HASH, apporvedHash);
		orderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
	}
	
	public List<Order> fetchOrdersById(List<String> walletIds){
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(walletIds));
		return orderReactiveMongoTemplate.find(query, Order.class).collectList().block();
	}
	
	public Order fetchOrderById(String id){
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).in(id));
		return orderReactiveMongoTemplate.find(query, Order.class).blockFirst();
	}
}
