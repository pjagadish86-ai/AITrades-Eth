package com.aitrades.blockchain.eth.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.domain.ApproveRequest;
import com.aitrades.blockchain.eth.gateway.service.ApproveProcessor;

@RestController
@RequestMapping("/snipe/api/v1")
public class ApproveController {
	
	@Autowired
	private ApproveProcessor approveProcessor;

	@PostMapping("/approve")
	public Object approve(@RequestBody ApproveRequest approveRequest) throws Exception {
		return approveProcessor.approve(approveRequest);
	}
}
