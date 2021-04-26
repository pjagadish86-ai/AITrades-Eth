package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.ApproveRequest;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;

@Service
public class ApproveProcessor {

	private static final String PROCESSED = "Acknowledged for approve";

	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	private SnipeOrderHistoryRepository orderHistoryRepository;
	
	public Object approve(ApproveRequest approveRequest) throws Exception {
		SnipeTransactionRequest snipeRequest  = orderHistoryRepository.fetchSnipeOrderById(approveRequest.getId());
		approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeRequest);
		return PROCESSED;
	}
	public Object approve(String id) throws Exception {
		SnipeTransactionRequest snipeRequest  = orderHistoryRepository.fetchSnipeOrderById(id);
		approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeRequest);
		return PROCESSED;
	}
}
