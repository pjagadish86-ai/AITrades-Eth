package com.aitrades.blockchain.eth.gateway.service;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;
import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.ApproveTransactionRepository;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;
import com.aitrades.blockchain.eth.gateway.web3j.PreApproveProcosser;
import com.aitrades.blockchain.eth.gateway.web3j.StrategyGasProvider;

@Service
public class ApprovedTransactionProcessor {

	private static final String BUY = "BUY";

	private static final String TILDA = "~";

	@Autowired
	private ApproveTransactionRepository approveTransactionRepository;
	
	@Autowired
	private OrderPreprosorChecks orderPreprosorChecks;
	
	@Autowired
	private PreApproveProcosser preApproveProcosser;
	
	@Autowired
	private StrategyGasProvider strategyGasProvider;
	
	public boolean checkAndProcessBuyApproveTransaction(Order order) throws Exception {
		String address  = order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY) ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress();
				
		ApproveTransaction approveTransaction = approveTransactionRepository.find(order.getWalletInfo().getPublicKey().toLowerCase() + TILDA + order.getRoute() +TILDA + address.toLowerCase()); // id should -> publickey ~ router ~ contractaddresss
		if(approveTransaction != null && approveTransaction.getApprovedHash() != null) {
			if(StringUtils.isBlank(approveTransaction.getStatus())) {
				Optional<TransactionReceipt> isApprovedSuccess  = orderPreprosorChecks.checkStatusOfApprovalTransaction(approveTransaction.getApprovedHash(), order.getRoute());
				if(isApprovedSuccess.isPresent()) {
					approveTransaction.setStatus("0x1");
					approveTransactionRepository.update(approveTransaction);
				}
			}else {
				return true;
			}
		}else {
			String hash  = preApproveProcosser.approve(order.getRoute(), order.getCredentials(), order.getTo().getTicker().getAddress(), strategyGasProvider, GasModeEnum.fromValue(order.getGasMode()), order.getGasPrice().getValueBigInteger());
			if(StringUtils.isNotBlank( hash)) {
				ApproveTransaction approveTrnx = new ApproveTransaction();
				approveTrnx.setId(order.getWalletInfo().getPublicKey().toLowerCase() + TILDA + order.getRoute() +TILDA +  address.toLowerCase());
				approveTrnx.setContractAddressInteracted( order.getTo().getTicker().getAddress());
				approveTrnx.setPublicKey(order.getWalletInfo().getPublicKey());
				approveTrnx.setApprovedHash(hash);
				approveTransactionRepository.insert(approveTrnx);
			}
		}
		return false;
	}
	
	public boolean checkAndProcessSnipeApproveTransaction(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		ApproveTransaction approveTransaction = approveTransactionRepository.find(snipeTransactionRequest.getWalletInfo().getPublicKey().toLowerCase() + TILDA + snipeTransactionRequest.getRoute() +TILDA + snipeTransactionRequest.getToAddress().toLowerCase()); // id should -> publickey ~ router ~ contractaddresss
		if(approveTransaction != null && approveTransaction.getApprovedHash() != null) {
			if(StringUtils.isBlank(approveTransaction.getStatus())) {
				Optional<TransactionReceipt> isApprovedSuccess  = orderPreprosorChecks.checkStatusOfApprovalTransaction(approveTransaction.getApprovedHash(), snipeTransactionRequest.getRoute());
				if(isApprovedSuccess.isPresent()) {
					approveTransaction.setStatus("0x1");
					approveTransactionRepository.update(approveTransaction);
				}
			}else {
				return true;
			}
		}else {
			String hash  = preApproveProcosser.approve(snipeTransactionRequest.getRoute(), snipeTransactionRequest.getCredentials(), snipeTransactionRequest.getToAddress(), strategyGasProvider, GasModeEnum.fromValue(snipeTransactionRequest.getGasMode()), snipeTransactionRequest.getGasPrice());
			if(StringUtils.isNotBlank( hash)) {
				ApproveTransaction approveTrnx = new ApproveTransaction();
				approveTrnx.setId(snipeTransactionRequest.getWalletInfo().getPublicKey().toLowerCase() + TILDA + snipeTransactionRequest.getRoute() +TILDA + snipeTransactionRequest.getToAddress().toLowerCase());
				approveTrnx.setContractAddressInteracted(snipeTransactionRequest.getToAddress());
				approveTrnx.setPublicKey(snipeTransactionRequest.getWalletInfo().getPublicKey());
				approveTrnx.setApprovedHash(hash);
				approveTransactionRepository.insert(approveTrnx);
			}
		}
		return false;
	}
}
