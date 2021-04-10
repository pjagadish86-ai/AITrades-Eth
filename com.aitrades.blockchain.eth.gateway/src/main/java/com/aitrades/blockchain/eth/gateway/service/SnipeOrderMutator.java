package com.aitrades.blockchain.eth.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Service
public class SnipeOrderMutator {
	
	private static final String AVAL = "AVAL";
	
	@Autowired
	private SnipeOrderProcessor snipeOrderProcessor;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		logger.info("in snipeorder mutator", snipeTransactionRequest);
		snipeTransactionRequest.setRead(AVAL);
		snipeOrderProcessor.snipeOrder(snipeTransactionRequest);
		return snipeTransactionRequest.getId();
	}
	
}
