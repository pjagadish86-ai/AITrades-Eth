package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;

public class SnipeOrderGatewayEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	public SnipeOrderRepository snipeOrderRepository;
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) throws Exception {
		for(SnipeTransactionRequest snipeTransactionRequest : transactionRequests) {
			if(checkStatus(snipeTransactionRequest)) {
				sendOrderToSnipe(snipeTransactionRequest);
			}
		}
	}

	private void sendOrderToSnipe(SnipeTransactionRequest snipeOrder) {
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) {
		try {
			boolean hasApprovedStatusSuccess = approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
			if(hasApprovedStatusSuccess) {
				snipeOrderRepository.updateLock(snipeTransactionRequest);
			}
			return hasApprovedStatusSuccess;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
