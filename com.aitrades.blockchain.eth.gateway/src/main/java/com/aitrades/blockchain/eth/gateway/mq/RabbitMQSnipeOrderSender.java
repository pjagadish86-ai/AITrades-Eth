package com.aitrades.blockchain.eth.gateway.mq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

@Service
public class RabbitMQSnipeOrderSender {
	
	@Resource(name="snipeOrderRabbitTemplate")
	public AmqpTemplate snipeOrderRabbitTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void send(SnipeTransactionRequest transactionRequest) {
		snipeOrderRabbitTemplate.convertAndSend(transactionRequest);
		logger.info("sniperorder sent to queue succesfully");
	}
}