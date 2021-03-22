package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.service.OrderProcessor;

@RestController
@RequestMapping("/api/v1")
public class CreateOrderController {

	@Autowired
	private OrderProcessor orderProcessor;
	
	@PostMapping("/createOrder")
	public String createOrder(@RequestBody Order order) {
		return orderProcessor.createOrder(order);
	}
	
	@PostMapping("/cancelOrder")
	public void cancelOrder() {
		
	}
	
	@PostMapping("/modifyOrder")
	public void modifyOrder() {
		
	}
	
}
