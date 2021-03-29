package com.aitrades.blockchain.eth.gateway.repository;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;

import reactor.core.publisher.Mono;

@Repository
public class ApproveTransactionRepository {

	@Resource(name = "approveTransactionMongoTemplate")
	public ReactiveMongoTemplate approveTransactionMongoTemplate;

	public Mono<ApproveTransaction> insert(ApproveTransaction approveTransaction) {
		return approveTransactionMongoTemplate.insert(approveTransaction);
	}

	public ApproveTransaction find(String id) {
		return approveTransactionMongoTemplate.findById(id, ApproveTransaction.class).block();
	}

}
