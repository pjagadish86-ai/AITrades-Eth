package com.aitrades.blockchain.eth.gateway.repository;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.mongodb.client.result.DeleteResult;

@Repository
public class SnipeOrderHistoryRepository {
	
	private static final String PUBLIC_KEY = "publicKey";
	private static final String ID = "id";
	
	@Resource(name = "snipeOrderHistoryReactiveMongoTemplate")
	public ReactiveMongoTemplate snipeOrderHistoryReactiveMongoTemplate;

	public void save(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		 snipeOrderHistoryReactiveMongoTemplate.save(snipeTransactionRequest).block();
	}
	
	public List<SnipeTransactionRequest> fetchOrdersById(List<String> walletIds){
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(walletIds));
		return snipeOrderHistoryReactiveMongoTemplate.find(query, SnipeTransactionRequest.class).collectList().block();
	}

	public SnipeTransactionRequest fetchAndRemoveSnipeOrderById(String parentOrderId) {
		Query query = new Query();
        query.addCriteria(Criteria.where(PUBLIC_KEY).in(parentOrderId));
        final SnipeTransactionRequest snipeTransactionRequest = snipeOrderHistoryReactiveMongoTemplate.find(query, SnipeTransactionRequest.class).blockFirst();
		if(snipeTransactionRequest != null) {
			delete(snipeTransactionRequest);
		}	
		return snipeTransactionRequest;
	}

	public void delete(SnipeTransactionRequest snipeTransactionRequest) {
		Query query = new Query();
        query.addCriteria(Criteria.where(ID).is(snipeTransactionRequest));
        DeleteResult deleteResult = snipeOrderHistoryReactiveMongoTemplate.remove(snipeTransactionRequest).block();
        if(deleteResult != null && deleteResult.getDeletedCount() == 0) {
        	System.out.println("retrigger snipe order not deleted");
		}else {
			System.out.println("retrigger snipe order deleted");
		}
	}
}
