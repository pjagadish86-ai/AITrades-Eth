package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;

public class RabbitMqSnipeOrderEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@Autowired
	private OrderPreprosorChecks statusChecker;

	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) {
		transactionRequests.parallelStream()//TODO: garbabage codeing
						   .filter(t -> {
							try {
								return checkStatus(t);
							} catch (Exception e) {
							}
							return false;
						})
						   .forEach(snipeOrder -> rabbitMQSnipeOrderSender.send(snipeOrder));
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		return approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
	}
}
