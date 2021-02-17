package com.aitrades.blockchain.gateway.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.gateway.domain.Order;
import com.aitrades.blockchain.gateway.domain.OrderRes;
import com.aitrades.blockchain.gateway.repository.OrderRepository;
import com.aitrades.blockchain.gateway.transformer.OrderTransformer;
import com.google.common.collect.Lists;

import reactor.core.publisher.Mono;

@Service
public class OrderMutator {
	
	@Autowired
	private OrderTransformer orderTransformer;
	
	@Autowired
	private OrderRepository orderRepository;
	
	public List<OrderRes> createOrder(Order order) {
		OrderRes orderRes = new OrderRes("success messgae for rcp");
		List<OrderRes> orderRess = Lists.newArrayList(orderRes);
		Mono<Order> insertedRecord = orderRepository.insert(order);
		System.out.println(insertedRecord.block().getId());
		return orderRess;
	}
	
}
