package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
		List<SnipeTransactionRequest> uniqueSnipeOrders = new ArrayList<SnipeTransactionRequest>(new LinkedHashSet<>(transactionRequests));
		for(SnipeTransactionRequest snipeTransactionRequest : uniqueSnipeOrders) {
			if(checkStatus(snipeTransactionRequest)) {
				sendOrderToSnipe(snipeTransactionRequest);
			}
		}
	}

	private synchronized void  sendOrderToSnipe(SnipeTransactionRequest snipeOrder) {
		snipeOrderRepository.saveWithUpdateLock(snipeOrder);
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) {
		try {
			return true;// approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
