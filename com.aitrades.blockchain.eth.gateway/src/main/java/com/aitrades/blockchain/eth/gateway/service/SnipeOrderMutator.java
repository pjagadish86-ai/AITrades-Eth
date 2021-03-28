package com.aitrades.blockchain.eth.gateway.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.web3j.PreApproveProcosser;
import com.aitrades.blockchain.eth.gateway.web3j.StrategyGasProvider;

@Service
public class SnipeOrderMutator {
	
	@Autowired
	private SnipeOrderProcessor snipeOrderProcessor;

	@Autowired
	private PreApproveProcosser preApproveProcosser;
	
	@Autowired
	private StrategyGasProvider strategyGasProvider;
	
	public String snipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		if(!snipeTransactionRequest.isPreApproved()) {
			String approvedHash = approveAndSaveSnipeOrder(snipeTransactionRequest);
			if(StringUtils.isNotBlank(approvedHash)) {
				snipeTransactionRequest.setApprovedHash(approvedHash);
			}else {
				throw new Exception("Unable to approve transaction please retry again !!!");
			}	
		}
		return snipeOrderProcessor.snipeOrder(snipeTransactionRequest);
	}
	
	public String approveAndSaveSnipeOrder(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		return preApproveProcosser.approve(snipeTransactionRequest.getRoute(), snipeTransactionRequest.getCredentials(), snipeTransactionRequest.getToAddress(), strategyGasProvider, GasModeEnum.valueOf(snipeTransactionRequest.getGasMode()));
	}
}
