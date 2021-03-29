package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

import reactor.core.publisher.Mono;

@Repository
public class SnipeOrderRepository {

	@Resource(name = "snipeOrderReactiveMongoTemplate")
	public ReactiveMongoTemplate snipeOrderReactiveMongoTemplate;

	public Mono<SnipeTransactionRequest> insert(SnipeTransactionRequest transactionRequest) {
		return snipeOrderReactiveMongoTemplate.insert(transactionRequest);
	}
	
	public void updateLock(SnipeTransactionRequest snipeTransactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where("id").is(snipeTransactionRequest.getId()));
        Update update = new Update();
        update.set("read", "LOCK");
        snipeOrderReactiveMongoTemplate.updateFirst(query, update, SnipeTransactionRequest.class).block();
	}
	
	public void updateAvail(SnipeTransactionRequest snipeTransactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where("id").is(snipeTransactionRequest.getId()));
        Update update = new Update();
        update.set("read", "AVAL");
     //   update.set("counter", order.getCounter()+1); TODO: Come back
        snipeOrderReactiveMongoTemplate.updateFirst(query, update, SnipeTransactionRequest.class).block();
	}
}
