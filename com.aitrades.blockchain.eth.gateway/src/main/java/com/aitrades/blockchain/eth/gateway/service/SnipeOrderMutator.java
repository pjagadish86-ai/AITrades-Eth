package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Service
public class SnipeOrderMutator {
	
	private static final String AVAL = "AVAL";
	
	@Autowired
	private SnipeOrderProcessor snipeOrderProcessor;

	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		snipeTransactionRequest.setRead(AVAL);
		snipeOrderProcessor.snipeOrder(snipeTransactionRequest);
		return snipeTransactionRequest.getId();
	}
	
}
