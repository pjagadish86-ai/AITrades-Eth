package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

public class GlobalErrorHandler {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ServiceActivator(inputChannel = "errorFlow")
	public void errorFlow(Message<?> message) throws Exception {
		logger.warn("Entered in errorflow");
		//System.out.println("@@@@@@@@@@@@@@@@@@@@@" + ((MessagingException) message.getPayload()).getFailedMessage().getPayload());
		try {
			
			Object object = ((MessagingException) message.getPayload()).getFailedMessage() != null
								&& ((MessagingException) message.getPayload()).getFailedMessage().getPayload() != null
								? ((MessagingException) message.getPayload()).getFailedMessage().getPayload()  
										: Collections.emptyList();
			if(object instanceof List && !((List)object).isEmpty()){
			    if(((List)object).get(0) instanceof Order){
			    	List<Order> orders  = (List<Order>)object;
			    	for(Order order : orders) {
			    		logger.warn("Exception occured for order id ", order.getId(), " attempting to read mode as avail");
						orderRepository.updateAvail(order);	
					}
			    }
			    
			    if(((List)object).get(0) instanceof SnipeTransactionRequest){
			    	List<SnipeTransactionRequest> snipeTransactionRequests  = (List<SnipeTransactionRequest>)object;
			    	for(SnipeTransactionRequest snipeTransactionRequest : snipeTransactionRequests) {
			    		logger.warn("Exception occured for snipeTransactionRequest id ", snipeTransactionRequest.getId(), " attempting to read mode as avail");
			    		snipeOrderRepository.updateAvail(snipeTransactionRequest);	
					}
			    
			    }
			}
		} catch (Exception e) {
			logger.error("Un recoverable exception happended here", e);
		}
	}
}
