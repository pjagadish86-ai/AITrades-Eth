package com.aitrades.blockchain.eth.gateway.mq;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.Order;

@Service
public class RabbitMQCreateOrderSender {
	
	@Resource(name="createOrderRabbitTemplate")
	public AmqpTemplate createOrderRabbitTemplate;
	
	@Async
	public void send(Order order) {
		createOrderRabbitTemplate.convertAndSend(order);
		System.out.println("Send msg = " + order);
	}
}