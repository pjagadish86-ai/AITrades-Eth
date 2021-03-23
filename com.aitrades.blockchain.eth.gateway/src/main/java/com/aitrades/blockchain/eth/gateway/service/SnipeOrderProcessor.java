package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

import reactor.core.publisher.Mono;

@Service
public class SnipeOrderProcessor {

	@Autowired
	public SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	
	public String snipeOrder(SnipeTransactionRequest transactionRequest) {
		Mono<SnipeTransactionRequest> insertedRecord = snipeOrderRepository.insert(transactionRequest);
		rabbitMQSnipeOrderSender.send(transactionRequest);
		return insertedRecord.block().getId();
	}

}
