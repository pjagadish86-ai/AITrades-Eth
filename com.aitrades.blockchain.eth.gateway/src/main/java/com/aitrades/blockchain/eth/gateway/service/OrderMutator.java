package com.aitrades.blockchain.eth.gateway.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.AuditInformation;
import com.aitrades.blockchain.eth.gateway.domain.Order;

@Service
public class OrderMutator {
	
	private static final String AVAL = "AVAL";
	@Autowired
	private OrderProcessor orderProcessor;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String createOrder(Order order) throws Exception {
		logger.info("in order mutator");
		order.setRead(AVAL);
		AuditInformation auditInformation = new AuditInformation(LocalDateTime.now().toString(), LocalDateTime.now().toString());
		order.setAuditInformation(auditInformation);
		orderProcessor.createOrder(order);
		return order.getId();
	}
	
}
