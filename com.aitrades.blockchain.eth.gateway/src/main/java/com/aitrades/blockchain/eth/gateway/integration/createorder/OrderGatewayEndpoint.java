package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.OrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;

public class OrderGatewayEndpoint {

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@Autowired
	private OrderPreprosorChecks pairDataRetriever;
	
	@SuppressWarnings("unused")
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;
	
	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) throws Exception {
		List<Order> uniqueOrders = new ArrayList<>(new LinkedHashSet<>(orders));
		for(Order order : uniqueOrders) {
			try {
				if (checkStatusAndLockMessage(order)) {
					if (order.getPairData() == null) {
						PairData pairData = populatePairData(order);
						if (pairData != null) {
							order.setPairData(pairData);
						}
					}
					if (order.getPairData() != null) {
						sendToQueueAndUpdateLock(order);
					} 
				}
			} catch (Exception e) {
				order.setErrorMessage(e.getMessage());
				orderHistoryRepository.save(order);
				orderRepository.delete(order);
				e.printStackTrace();
			}
		}
		return orders;
	}

	private boolean checkStatusAndLockMessage(Order order) throws Exception {
		return order.isApproveStatusCheck() ? approvedTransactionProcessor.checkAndProcessBuyApproveTransaction(order) : true;
	}
	
	private PairData populatePairData(Order order) {
		return pairDataRetriever.getPairData(order);
	}
	
	private synchronized void sendToQueueAndUpdateLock(Order order) {
		orderRepository.updateLock(order);
		rabbitMQCreateOrderSender.send(order);
	}
}
