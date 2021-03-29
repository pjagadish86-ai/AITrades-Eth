package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Service
public class SnipeOrderMutator {
	
	@Autowired
	private SnipeOrderProcessor snipeOrderProcessor;

	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		snipeTransactionRequest.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		snipeTransactionRequest.setRead("AVAL");
		return snipeOrderProcessor.snipeOrder(snipeTransactionRequest);
	}
	
}
