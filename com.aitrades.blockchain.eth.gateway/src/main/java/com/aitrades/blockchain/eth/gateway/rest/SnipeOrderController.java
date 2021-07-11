package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.RetriggerSnipeOrder;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.service.RetriggerSnipeOrderPreparer;
import com.aitrades.blockchain.eth.gateway.service.SnipeOrderMutator;
import com.aitrades.blockchain.eth.gateway.validator.RestExceptionMessage;
import com.aitrades.blockchain.eth.gateway.validator.SnipeOrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/snipe/api/v1")
public class SnipeOrderController {

	@Autowired
	private SnipeOrderMutator snipeOrderMutator;
	
	@Autowired
	private RetriggerSnipeOrderPreparer retriggerOrderPreparer;
	
	@Autowired
	private SnipeOrderValidator snipeOrderValidator;
	
    private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping("/snipeOrder")
	public Object createOrder(@RequestBody SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		String id = UUIDGenerator.nextHex(UUIDGenerator.TYPE1);
		snipeTransactionRequest.setId(id);
		logger.info("New Order id->", id);
		RestExceptionMessage restExceptionMessage = snipeOrderValidator.validateSnipeOrder(snipeTransactionRequest);
		if(restExceptionMessage != null) {
			return new ResponseEntity<RestExceptionMessage>(restExceptionMessage, HttpStatus.BAD_REQUEST);
		}
		return snipeOrderMutator.mutateSnipeOrder(snipeTransactionRequest);
	}
	
	@PostMapping("/retriggerOrder")
	public Object retriggerOrder(@RequestBody RetriggerSnipeOrder retriggerOrder) throws Exception {
		RestExceptionMessage restExceptionMessage = snipeOrderValidator.validateRetriggerOrderSnipeOrder(retriggerOrder);
		if(restExceptionMessage != null) {
			return new ResponseEntity<RestExceptionMessage>(restExceptionMessage, HttpStatus.BAD_REQUEST);
		}
		return retriggerOrderPreparer.retriggerOrder(retriggerOrder);
	}
	
	@PostMapping("/cancelSnipeOrder")
	public void cancelOrder() {
		
	}
	
	@PostMapping("/modifySnipeOrder")
	public void modifyOrder() {
		
	}
	
}
