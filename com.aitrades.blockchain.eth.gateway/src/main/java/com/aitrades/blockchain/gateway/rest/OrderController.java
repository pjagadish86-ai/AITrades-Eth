package com.aitrades.blockchain.gateway.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.gateway.domain.Order;
import com.aitrades.blockchain.gateway.domain.OrderRes;
import com.aitrades.blockchain.gateway.service.OrderMutator;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderMutator orderMutator;
	
	@PostMapping("/createOrder")
	public List<OrderRes> createOrder(@RequestBody Order order) {
		return orderMutator.createOrder(order);
	}
	
	@PostMapping("/cancelOrder")
	public void cancelOrder() {
		
	}
	
}
