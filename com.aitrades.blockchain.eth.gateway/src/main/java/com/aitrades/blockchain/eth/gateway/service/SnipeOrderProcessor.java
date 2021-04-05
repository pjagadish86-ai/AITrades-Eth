package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

import reactor.core.publisher.Mono;

@Service
public class SnipeOrderProcessor {

	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
		Mono<SnipeTransactionRequest> insertedRecord = snipeOrderRepository.insert(snipeTransactionRequest);
		return insertedRecord.block().getId();
	}

}
