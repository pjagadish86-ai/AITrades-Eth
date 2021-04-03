package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;

public class OrderGatewayEndpoint {

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@Autowired
	private OrderPreprosorChecks statusChecker;
	
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	private OrderRepository orderRepository;

	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) throws Exception {
		List<Order> uniqueOrders = new ArrayList<>(new LinkedHashSet<>(orders));
		for(Order order : uniqueOrders) {
			if(checkStatusAndLockMessage(order)) {
				sendGetPairData(order);
				sendToQueueAndUpdateLock(order);
			}
		}
		return orders;
	}

	private boolean checkStatusAndLockMessage(Order order) throws Exception {
		return approvedTransactionProcessor.checkAndProcessBuyApproveTransaction(order);
	}
	
	private void sendGetPairData(Order order) {
		PairData pairData  = statusChecker.getPairData(order);
		if(pairData != null) {
			order.setPairData(pairData);
		}
	}
	
	private synchronized void sendToQueueAndUpdateLock(Order order) {
		orderRepository.updateLock(order);
		rabbitMQCreateOrderSender.send(order);
	}
}
