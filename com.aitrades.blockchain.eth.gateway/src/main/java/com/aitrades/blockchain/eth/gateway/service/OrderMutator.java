package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.Order;

@Service
public class OrderMutator {
	
	private static final String AVAL = "AVAL";
	@Autowired
	private OrderProcessor orderProcessor;

	public String createOrder(Order order) throws Exception {
		String id = UUIDGenerator.nextHex(UUIDGenerator.TYPE1);
		order.setId(id);
		order.setRead(AVAL);
		orderProcessor.createOrder(order);
		return id;
	}
	
}
