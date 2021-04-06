package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQSnipeOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

public class SnipeOrderGatewayEndpoint {

	@Autowired
	private RabbitMQSnipeOrderSender rabbitMQSnipeOrderSender;

	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private SnipeOrderHistoryRepository snipeOrderHistoryRepository;
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) throws Exception {
		List<SnipeTransactionRequest> uniqueSnipeOrders = new ArrayList<>(new LinkedHashSet<>(transactionRequests));
		for(SnipeTransactionRequest snipeTransactionRequest : uniqueSnipeOrders) {
			try {
				if(snipeTransactionRequest.isExeTimeCheck()) {
					boolean timeMet = executionTimeMet(snipeTransactionRequest.getExecutionTime());
					if(timeMet) {
						sendOrderToSnipe(snipeTransactionRequest);
					}
				}else {
					sendOrderToSnipe(snipeTransactionRequest);
				}
			} catch (Exception e) {
				snipeTransactionRequest.setErrorMessage(e.getMessage());
				snipeOrderHistoryRepository.save(snipeTransactionRequest);
				snipeOrderRepository.delete(snipeTransactionRequest);
				e.printStackTrace();
			}
		}
	}
	
	private boolean executionTimeMet(String executionTime) {
	    LocalDateTime dateTime = LocalDateTime.parse(executionTime);
	    LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
		return now.isAfter(dateTime);
	}

	private void  sendOrderToSnipe(SnipeTransactionRequest snipeOrder) {
		snipeOrderRepository.saveWithUpdateLock(snipeOrder);
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
}
