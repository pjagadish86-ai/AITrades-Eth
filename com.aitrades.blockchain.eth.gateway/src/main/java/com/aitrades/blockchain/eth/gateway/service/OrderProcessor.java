package com.aitrades.blockchain.eth.gateway.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;

import reactor.core.publisher.Mono;

@Service
public class OrderProcessor {
	
	@Autowired
	public OrderRepository orderRepository;

	// order code 80 = accepted order
	// order code 81 = pre processing order
	// order code 82 = pre processing failed order
	// order code 83 = retry order didn't meet criteria 
	// order code 84 = send order
	// order code 85 = close order
	
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String createOrder(Order order) throws Exception {
		logger.info("in order processor ");
		order.setOrderCode(83);
		approvedTransactionProcessor.checkAndProcessBuyApproveTransaction(order);
		order.setApproveStatusCheck(false);
		if(StringUtils.isNotBlank(order.getApprovedHash()) 
				&& OrderSide.SELL.name().equalsIgnoreCase(order.getOrderEntity().getOrderSide())) {
			logger.info("in order processor checking for sellorder approved hash");
			order.setApproveStatusCheck(true);
		}
		Mono<Order> insertedRecord = orderRepository.insert(order);
		return insertedRecord.block().getId();
	}
	
}
