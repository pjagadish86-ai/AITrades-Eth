package com.aitrades.blockchain.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.gateway.domain.Token;

@Service
public class TokenInformationRetriever {
	
	@Autowired
	private AccountInfoClient accountInfoClient;

	public List<Token> retrieveTokenInformation(String publicAddress) throws Exception {
		List<Token>  tokens = new ArrayList<Token>();
		Token token = new Token( accountInfoClient.getAccountBalance(publicAddress));
		tokens.add(token);
		return tokens;
	}

	
}
