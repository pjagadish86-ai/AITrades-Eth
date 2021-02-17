package com.aitrades.blockchain.gateway.service;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountInfoClient {

	protected final Web3j web3j;
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountInfoClient.class);

	public AccountInfoClient(final Web3j web3j,final  RestTemplate restTemplate,final ObjectMapper objectMapper) {
		this.web3j = web3j;
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
	}
	
	public BigInteger getAccountBalance(String publicAddress) throws Exception {
		LOGGER.info("entered into accountbalance");
		EthGetBalance ethGetBalance = web3j.ethGetBalance(publicAddress, DefaultBlockParameterName.LATEST)
										   .sendAsync()
										   .get();
		return ethGetBalance.getBalance();
	}
	
}
