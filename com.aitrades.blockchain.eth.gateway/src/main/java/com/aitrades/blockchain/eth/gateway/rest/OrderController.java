package com.aitrades.blockchain.eth.gateway.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.service.OrderMutator;
import com.aitrades.blockchain.eth.gateway.validator.OrderValidator;
import com.aitrades.blockchain.eth.gateway.validator.RestExceptionMessage;

@RestController
@RequestMapping("/order/api/v1")
public class OrderController {

	@Autowired
	private OrderMutator orderMutator;
	
	@Autowired
	private OrderValidator orderValidator;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PostMapping("/createOrder")
	public Object createOrder(@RequestBody Order order) throws Exception {
		String id = UUIDGenerator.nextHex(UUIDGenerator.TYPE1);
		order.setId(id);
		logger.info("in create order={}", order);
		RestExceptionMessage exceptionMessage  = orderValidator.validatorOrder(order);// we should check balance for buy and sell and skip only when we have parentid not null
		if(exceptionMessage != null) {
			logger.error(" order is invalid for create order={}, validation message={}", order, exceptionMessage);
			return new ResponseEntity<RestExceptionMessage>(exceptionMessage, HttpStatus.BAD_REQUEST);
		}
		return orderMutator.createOrder(order);
	}
	
	@PostMapping("/cancelOrder")
	public void cancelOrder() {
		
	}
	
	@PostMapping("/modifyOrder")
	public void modifyOrder() {
		
	}
	
}
