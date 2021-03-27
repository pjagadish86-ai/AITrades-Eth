package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.web3j.ApprovedTransactionStatusChecker;

public class RabbitMqSnipeOrderEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@Autowired
	private ApprovedTransactionStatusChecker statusChecker;

	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) {
		transactionRequests.parallelStream()//TODO: garbabage codeing
						   .filter(this :: checkStatus)
						   .forEach(snipeOrder -> rabbitMQSnipeOrderSender.send(snipeOrder));
	}
	
	public boolean checkStatus(SnipeTransactionRequest snipeTransactionRequest) {
		if(snipeTransactionRequest.isPreApproved()) {
			return true;
		}
		return !snipeTransactionRequest.hasSniped() 
				&& StringUtils.isNotBlank(snipeTransactionRequest.getApprovedHash())
				&& statusChecker.checkStatusOfApprovalTransaction(snipeTransactionRequest.getApprovedHash()).isPresent();
	}
}
