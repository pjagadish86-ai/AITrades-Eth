package com.aitrades.blockchain.eth.gateway.integration.createorder;

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
	public OrderRepository orderRepository;

	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) throws Exception {
		for(Order order : orders) {
			if(checkStatusAndLockMessage(order)) {
				sendToCreateOrderQueue(order);
			}
		}
		return orders;
	}

	private boolean checkStatusAndLockMessage(Order order) throws Exception {
		try {
			boolean hasApprovedStatusSuccess = approvedTransactionProcessor.checkAndProcessBuyApproveTransaction(order);
			if(hasApprovedStatusSuccess) {
				orderRepository.updateLock(order);
			}
			return hasApprovedStatusSuccess;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void sendToCreateOrderQueue(Order order) {
		System.out.println("in addNewOrderToRabbitMq");
		PairData pairData  = statusChecker.getPairData(order);
		if(pairData != null) {
			order.setPairData(pairData);
			rabbitMQCreateOrderSender.send(order);
		}
	}
}
