package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;

public class RabbitMqCreateOrderEndpoint {

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@Autowired
	private OrderPreprosorChecks statusChecker;

	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) {
		System.out.println("in addNewOrderToRabbitMq");
		orders.parallelStream()
			  .filter(this :: checkStatus)
			  .forEach(order -> sendToCreateOrderQueue(order));
		return orders;
	}

	private void sendToCreateOrderQueue(Order order) {
		PairData pairData  = statusChecker.getPairData(order);
		if(pairData != null) {
			order.setPairData(pairData);
			rabbitMQCreateOrderSender.send(order);
		}
	}
	
	public boolean checkStatus(Order order) {
		return StringUtils.isNotBlank(order.getApprovedHash())
				&& statusChecker.checkStatusOfApprovalTransaction(order.getApprovedHash(), order.getRoute())
								.isPresent();
	}
}
