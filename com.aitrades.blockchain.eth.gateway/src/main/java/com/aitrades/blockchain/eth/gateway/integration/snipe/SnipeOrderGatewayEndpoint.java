package com.aitrades.blockchain.eth.gateway.integration.snipe;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ServiceActivator(inputChannel = "addSnipeOrderToRabbitMq")
	public void addSnipeOrderToRabbitMq(List<SnipeTransactionRequest> transactionRequests) throws Exception {
		logger.info("started processing addSnipeOrderToRabbitMq");
		List<SnipeTransactionRequest> uniqueSnipeOrders = new ArrayList<>(new LinkedHashSet<>(transactionRequests));
		for(SnipeTransactionRequest snipeTransactionRequest : uniqueSnipeOrders) {
			try {
				String id = snipeTransactionRequest.getId();
				logger.info("started processing addSnipeOrderToRabbitMq for snipeorder id={}", id);
				if(snipeTransactionRequest.isExeTimeCheck()) {
					logger.info("Snipe order has execution time order id={}", id);
					boolean timeMet = executionTimeMet(snipeTransactionRequest.getExecutionTime());
					if(timeMet) {
						logger.info("Snipe order has execution time order  id={} timeMet={}", id, timeMet);
						sendOrderToSnipe(snipeTransactionRequest);
					}
				}else {
					logger.info("Sending order to snipe downstreams id={}", id);
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
		logger.info("started processing sendOrderToSnipe");
		snipeOrderRepository.saveWithUpdateLock(snipeOrder);
		rabbitMQSnipeOrderSender.send(snipeOrder);
	}
	
}
