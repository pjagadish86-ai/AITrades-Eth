package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.web3j.ApprovedTransactionStatusChecker;

public class RabbitMqCreateOrderEndpoint {

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@Autowired
	private ApprovedTransactionStatusChecker statusChecker;

	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) {
		System.out.println("in addNewOrderToRabbitMq");
		orders.parallelStream()
			  .filter(this :: checkStatus)
			  .forEach(order -> rabbitMQCreateOrderSender.send(order));
		return orders;
	}
	
	public boolean checkStatus(Order order) {
		return StringUtils.isNotBlank(order.getApprovedHash())
				&& statusChecker.checkStatusOfApprovalTransaction(order.getApprovedHash()).isPresent();
	}
}
