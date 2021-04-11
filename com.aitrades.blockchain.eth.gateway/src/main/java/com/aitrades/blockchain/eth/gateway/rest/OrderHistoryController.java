package com.aitrades.blockchain.eth.gateway.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistories;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistoryRequest;
import com.aitrades.blockchain.eth.gateway.service.OrderHistoriesRetriever;
import com.aitrades.blockchain.eth.gateway.validator.OrderHistoryValidator;
import com.aitrades.blockchain.eth.gateway.validator.RestExceptionMessage;

@RestController
@RequestMapping("/order/api/v1")
public class OrderHistoryController {
	
	@Autowired
	private OrderHistoriesRetriever orderHistoriesRetriever;
	
	@Autowired
	private OrderHistoryValidator orderHistoryValidator;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());


	@PostMapping("/orderHistory")
	public OrderHistories fetchOrderHistories(OrderHistoryRequest orderHistoryRequest) throws Exception {
 
		RestExceptionMessage validationMsg = orderHistoryValidator.validateOrderHistoryRequest(orderHistoryRequest);
		if(validationMsg !=  null) {
			logger.error("validation occured for history request ", orderHistoryRequest, validationMsg);
		}
		return orderHistoriesRetriever.fetchOrderHistories(orderHistoryRequest);
	}
}
