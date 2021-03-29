package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.service.SnipeOrderMutator;

@RestController
@RequestMapping("/snipe/api/v1")
public class SnipeOrderController {

	@Autowired
	private SnipeOrderMutator snipeOrderMutator;
	
	@PostMapping("/snipeOrder")
	public String createOrder(@RequestBody SnipeTransactionRequest transactionRequest) throws Exception {
		return snipeOrderMutator.snipeOrder(transactionRequest);
	}
	
	@PostMapping("/cancelSnipeOrder")
	public void cancelOrder() {
		
	}
	
	@PostMapping("/modifySnipeOrder")
	public void modifyOrder() {
		
	}
	
}
