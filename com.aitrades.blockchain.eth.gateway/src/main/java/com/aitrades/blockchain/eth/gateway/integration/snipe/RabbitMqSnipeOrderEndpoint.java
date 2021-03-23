package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;

public class RabbitMqSnipeOrderEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) {
		System.out.println("in addSnipeOrderToRabbitMq");
		transactionRequests.parallelStream()
			  			   .forEach(transactionRequest -> rabbitMQSnipeOrderSender.send(transactionRequest));
	}
}
