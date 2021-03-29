package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;

@Repository
public class ApproveTransactionRepository {

	@Resource(name = "approveTransactionMongoTemplate")
	public ReactiveMongoTemplate approveTransactionMongoTemplate;

	public ApproveTransaction insert(ApproveTransaction approveTransaction) {
		return approveTransactionMongoTemplate.insert(approveTransaction).block();
	}

	public ApproveTransaction find(String id) {
		return approveTransactionMongoTemplate.findById(id, ApproveTransaction.class).block();
	}

	public void update(ApproveTransaction approveTransaction) {
		approveTransactionMongoTemplate.save(approveTransaction).block();
	}

}
