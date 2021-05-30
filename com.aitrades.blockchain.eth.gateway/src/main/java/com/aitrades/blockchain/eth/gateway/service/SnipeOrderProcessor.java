package com.aitrades.blockchain.eth.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

import reactor.core.publisher.Mono;

@Service
public class SnipeOrderProcessor {

	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@SuppressWarnings("unused")
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		logger.info("in SnipeOrderProcessor mutator", snipeTransactionRequest);
	//	approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
		Mono<SnipeTransactionRequest> insertedRecord = snipeOrderRepository.insert(snipeTransactionRequest);
		return insertedRecord.block().getId();
	}

}
