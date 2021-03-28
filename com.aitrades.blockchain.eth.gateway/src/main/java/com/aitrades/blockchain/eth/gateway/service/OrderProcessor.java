package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;

import reactor.core.publisher.Mono;

@Service
public class OrderProcessor {
	
	@Autowired
	public OrderRepository orderRepository;
	
	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;
	// order code 80 = accepted order
	// order code 81 = pre processing order
	// order code 82 = pre processing failed order
	// order code 83 = retry order didn't meet criteria 
	// order code 84 = send order
	// order code 85 = close order
	
	public String createOrder(Order order) {
		order.setOrderCode(83);
		Mono<Order> insertedRecord = orderRepository.insert(order);
		rabbitMQCreateOrderSender.send(order);
		return insertedRecord.block().getId();
	}
	
}
