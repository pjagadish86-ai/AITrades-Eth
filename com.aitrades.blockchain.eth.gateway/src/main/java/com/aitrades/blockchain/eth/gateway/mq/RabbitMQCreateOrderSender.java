package com.aitrades.blockchain.eth.gateway.mq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.Order;

@Service
public class RabbitMQCreateOrderSender {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource(name="createOrderRabbitTemplate")
	public AmqpTemplate createOrderRabbitTemplate;
	
	public void send(Order order) {
		createOrderRabbitTemplate.convertAndSend(order);
		logger.info("message sent to queue");
	}
}