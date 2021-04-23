package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

@Service
public class RetriggerSnipeOrderProcessor {

	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private SnipeOrderHistoryRepository snipeOrderHistoryRepository;

	public SnipeTransactionRequest fetchParentSnipeOrderById(String parentOrderId) {
		return snipeOrderHistoryRepository.fetchAndRemoveSnipeOrderById(parentOrderId);
	}

	public SnipeTransactionRequest saveSnipeOrder(SnipeTransactionRequest snipeOrder) {
		return snipeOrderRepository.insert(snipeOrder).block();
	}
	
}
