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
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

@Repository
public class SnipeOrderRepository {

	private static final String AVAL = "AVAL";
	private static final String NO = "NO";
	private static final String LOCK = "LOCK";
	private static final String READ = "read";
	private static final String ID = "id";
	private static final String APPROVED_HASH = "approvedHash";
	private static final String PUBLIC_KEY = "publicKey";
	
	@Resource(name = "snipeOrderReactiveMongoTemplate")
	private ReactiveMongoTemplate snipeOrderReactiveMongoTemplate;

	public Mono<SnipeTransactionRequest> insert(SnipeTransactionRequest transactionRequest) {
		return snipeOrderReactiveMongoTemplate.insert(transactionRequest);
	}
	
	public void delete(SnipeTransactionRequest transactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(transactionRequest.getId()));
        DeleteResult deleteResult = snipeOrderReactiveMongoTemplate.remove(transactionRequest).block();
        if(deleteResult != null && deleteResult.getDeletedCount() == 0) {
        	System.out.println("not deleted or not available");
		}else {
			System.out.println("deleted");
		}
	}
	
	public SnipeTransactionRequest find(SnipeTransactionRequest transactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(transactionRequest.getId()));
		return snipeOrderReactiveMongoTemplate.findOne(query, SnipeTransactionRequest.class).block();
	}
	
	public void updateLock(SnipeTransactionRequest snipeTransactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(snipeTransactionRequest.getId()));
        Update update = new Update();
        update.set(READ, LOCK);
        UpdateResult updateResult = snipeOrderReactiveMongoTemplate.updateFirst(query, update, SnipeTransactionRequest.class).block();
        if(updateResult != null && updateResult.getMatchedCount() == 0) {
			System.out.println("not updated");
		}else {
			System.out.println("updated");
		}
	}
	
	public void saveWithUpdateLock(SnipeTransactionRequest transactionRequest) {
		transactionRequest.setRead(LOCK);
		transactionRequest.setRetryEnabled(NO);
		transactionRequest.getAuditInformation().setUpdatedDateTime(LocalDateTime.now().toString());
		snipeOrderReactiveMongoTemplate.save(transactionRequest).block();
	}
	
	public void updateAvail(SnipeTransactionRequest snipeTransactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(snipeTransactionRequest.getId()));
        Update update = new Update();
        update.set(READ, AVAL);
        snipeOrderReactiveMongoTemplate.updateFirst(query, update, SnipeTransactionRequest.class).block();
	}

	public void updateApprovedHash(SnipeTransactionRequest snipeTransactionRequest, String apporvedHash) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(snipeTransactionRequest.getId()));
        Update update = new Update();
        update.set(APPROVED_HASH, apporvedHash);
        snipeOrderReactiveMongoTemplate.updateFirst(query, update, Order.class).block();
		
	}

	public List<SnipeTransactionRequest> fetchOrdersById(List<String> walletIds) {
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(walletIds));
		return snipeOrderReactiveMongoTemplate.find(query, SnipeTransactionRequest.class).collectList().block();
	}

	public SnipeTransactionRequest fetchSnipeOrderById(String parentOrderId) {
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(parentOrderId));
		return snipeOrderReactiveMongoTemplate.find(query, SnipeTransactionRequest.class).blockFirst();
	}
}
