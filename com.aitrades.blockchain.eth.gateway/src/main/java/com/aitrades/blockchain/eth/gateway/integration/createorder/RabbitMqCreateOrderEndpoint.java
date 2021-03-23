package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;

public class RabbitMqCreateOrderEndpoint {

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) {
		System.out.println("in addNewOrderToRabbitMq");
		orders.parallelStream()
			  .forEach(order -> rabbitMQCreateOrderSender.send(order));
		return orders;
	}
}
