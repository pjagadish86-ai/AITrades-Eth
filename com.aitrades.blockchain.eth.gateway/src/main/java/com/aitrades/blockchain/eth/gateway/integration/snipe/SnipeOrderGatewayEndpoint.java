package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;

public class SnipeOrderGatewayEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@SuppressWarnings("unused")
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private SnipeOrderHistoryRepository snipeOrderHistoryRepository;
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) throws Exception {
		List<SnipeTransactionRequest> uniqueSnipeOrders = new ArrayList<>(new LinkedHashSet<>(transactionRequests));
		for(SnipeTransactionRequest snipeTransactionRequest : uniqueSnipeOrders) {
			try {
				sendOrderToSnipe(snipeTransactionRequest);
			} catch (Exception e) {
				snipeTransactionRequest.setErrorMessage(e.getMessage());
				snipeOrderHistoryRepository.save(snipeTransactionRequest);
				snipeOrderRepository.delete(snipeTransactionRequest);
				e.printStackTrace();
			}
		}
	}

	private synchronized void  sendOrderToSnipe(SnipeTransactionRequest snipeOrder) {
		snipeOrderRepository.saveWithUpdateLock(snipeOrder);
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		return true;// approvedTransactionProcessor.checkAndProcessSnipeApproveTransaction(snipeTransactionRequest);
	}
}
