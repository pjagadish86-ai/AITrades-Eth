package com.aitrades.blockchain.eth.gateway.service;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.AuditInformation;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.web3j.EthereumDexContract;

@Service
public class SnipeOrderMutator {
	
	private static final String AVAL = "AVAL";
	
	@Autowired
	private SnipeOrderProcessor snipeOrderProcessor;

	@Autowired
	private EthereumDexContract ethereumDexContract;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String mutateSnipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		logger.info("in snipeorder mutator", snipeTransactionRequest);
		snipeTransactionRequest.setRead(AVAL);
		AuditInformation auditInformation = new AuditInformation(LocalDateTime.now().toString(), LocalDateTime.now().toString());
		snipeTransactionRequest.setAuditInformation(auditInformation);
		BigInteger decimals = ethereumDexContract.getDecimals(snipeTransactionRequest.getToAddress(), snipeTransactionRequest.getRoute(), snipeTransactionRequest.getCredentials());
		snipeTransactionRequest.setToAddressDecimals(decimals.toString());
//		final BigInteger outputTokens  = snipeTransactionRequest.getExpectedOutPutToken();
//		if(outputTokens != null && outputTokens.compareTo(BigInteger.ZERO) >0) {
//			try {
//				snipeTransactionRequest.setExpectedOutPutToken(Convert.toWei(outputTokens.toString(), Convert.Unit.fromString(TradeConstants.DECIMAL_MAP.get(decimals.toString()))).setScale(0, RoundingMode.DOWN).toBigInteger());
//			} catch (Exception e) {
//				e.printStackTrace();
//				snipeTransactionRequest.setExpectedOutPutToken(BigInteger.ONE);
//			}
//		}
	//	snipeTransactionRequest.setFeeEligible(true);
		snipeTransactionRequest.setExpectedOutPutToken(BigInteger.ZERO);
		snipeOrderProcessor.snipeOrder(snipeTransactionRequest);
		return snipeTransactionRequest.getId();
	}
	
}
