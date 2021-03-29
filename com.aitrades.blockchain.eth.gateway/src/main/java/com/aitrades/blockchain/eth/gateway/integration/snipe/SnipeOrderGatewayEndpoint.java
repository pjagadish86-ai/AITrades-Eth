package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;

public class SnipeOrderGatewayEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) throws Exception {
		throw new Exception("snipe");
//		for(SnipeTransactionRequest snipeTransactionRequest : transactionRequests) {
//			if(checkStatus(snipeTransactionRequest)) {
//				sendOrderToSnipe(snipeTransactionRequest);
//			}
//		}
	}

	private void sendOrderToSnipe(SnipeTransactionRequest snipeOrder) {
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) {
		try {
			return approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
