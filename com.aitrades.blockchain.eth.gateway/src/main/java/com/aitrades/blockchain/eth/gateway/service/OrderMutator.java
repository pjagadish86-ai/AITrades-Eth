package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.Order;

@Service
public class OrderMutator {
	
	@Autowired
	private OrderProcessor orderProcessor;

	public String createOrder(Order order) throws Exception {
		order.setId(UUIDGenerator.nextHex(UUIDGenerator.TYPE1));
		return orderProcessor.createOrder(order);
	}
	
}
