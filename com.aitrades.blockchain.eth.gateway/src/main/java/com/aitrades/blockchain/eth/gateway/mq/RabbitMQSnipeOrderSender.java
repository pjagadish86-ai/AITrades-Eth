package com.aitrades.blockchain.eth.gateway.mq;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Service
public class RabbitMQSnipeOrderSender {
	
	@Resource(name="snipeOrderRabbitTemplate")
	public AmqpTemplate snipeOrderRabbitTemplate;
	
	public void send(SnipeTransactionRequest transactionRequest) {
		snipeOrderRabbitTemplate.convertAndSend(transactionRequest);
		System.out.println("Snipe Message Sen" + transactionRequest);
	}
}