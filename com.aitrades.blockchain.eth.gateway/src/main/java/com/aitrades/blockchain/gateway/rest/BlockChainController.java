package com.aitrades.blockchain.gateway.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.gateway.domain.Token;
import com.aitrades.blockchain.gateway.service.TokenInformationRetriever;

@RestController
@RequestMapping("/api")
public class BlockChainController {
	
	@Autowired
	private TokenInformationRetriever tokenInformationRetriever;
	
	@PostMapping("/transaction")
	public Object execute(@RequestBody String transaction) throws Exception {
		return "hello";
	}
	
	
	@GetMapping("/getBalance/{publicAddress}")
	public List<Token> getBalance(@PathVariable(value = "publicAddress") final String publicAddress) throws Exception {
		return tokenInformationRetriever.retrieveTokenInformation(publicAddress);
	}
	
}
