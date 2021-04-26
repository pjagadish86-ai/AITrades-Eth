package com.aitrades.blockchain.eth.gateway.service;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;
import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.ApproveTransactionRepository;
import com.aitrades.blockchain.eth.gateway.repository.OrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;
import com.aitrades.blockchain.eth.gateway.web3j.PreApproveProcosser;
import com.aitrades.blockchain.eth.gateway.web3j.StrategyGasProvider;

@Service
public class ApprovedTransactionProcessor {

	private static final String _0X1 = "0x1";

	private static final String BUY = "BUY";

	private static final String TILDA = "~";
	
	private static final String _0X0 = "0x0";

	@Autowired
	private ApproveTransactionRepository approveTransactionRepository;
	
	@Autowired
	private OrderProcessorPrechecker orderPreprosorChecks;
	
	@Autowired
	private PreApproveProcosser preApproveProcosser;
	
	@Autowired
	private StrategyGasProvider strategyGasProvider;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;
	
	@SuppressWarnings("unused")
	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private SnipeOrderHistoryRepository snipeOrderHistoryRepository;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public boolean checkAndProcessBuyApproveTransaction(Order order) throws Exception {
		String address  = order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY) ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress();
				
		ApproveTransaction approveTransaction = approveTransactionRepository.find(order.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + order.getRoute().trim() +TILDA + address.toLowerCase().trim()); // id should -> publickey ~ router ~ contractaddresss
		if(approveTransaction != null && approveTransaction.getApprovedHash() != null) {
			if(StringUtils.isBlank(approveTransaction.getStatus())) {
				Optional<TransactionReceipt> transactionRecieptOptional  = orderPreprosorChecks.checkTransactionHashSuccess(approveTransaction.getApprovedHash(), order.getRoute());
				if(transactionRecieptOptional.isPresent()) {
					if(StringUtils.equalsIgnoreCase(transactionRecieptOptional.get().getStatus(), _0X0)) {
						approveTransactionRepository.delete(approveTransaction);
						order.setErrorMessage("APPROVE FAILED");
						orderHistoryRepository.save(order);
						orderRepository.delete(order);
					}else {
						approveTransaction.setStatus(_0X1);
						approveTransactionRepository.update(approveTransaction);
					}
				}
			}else {
				if(StringUtils.isBlank(order.getApprovedHash())) {
					order.setApprovedHash(approveTransaction.getApprovedHash());
				}
				return true;
			}
		}else {
			String hash  = preApproveProcosser.approve(order.getId(), order.getRoute(), order.getCredentials(), order.getTo().getTicker().getAddress(), strategyGasProvider, GasModeEnum.fromValue(order.getGasMode()), order.getGasPrice().getValueBigInteger(), order.getGasLimit().getValueBigInteger());
			if(StringUtils.isNotBlank( hash)) {
				ApproveTransaction approveTrnx = new ApproveTransaction();
				approveTrnx.setId(order.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + order.getRoute().trim() +TILDA +  address.toLowerCase().trim());
				approveTrnx.setContractAddressInteracted( order.getTo().getTicker().getAddress().trim());
				approveTrnx.setPublicKey(order.getWalletInfo().getPublicKey().trim());
				approveTrnx.setApprovedHash(hash);
				orderRepository.updateApprovedHash(order, hash);
				approveTransactionRepository.insert(approveTrnx);
			}
		}
		return false;
	}
	
	public boolean checkAndProcessSnipeApproveTransaction(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		ApproveTransaction approveTransaction = approveTransactionRepository.find(snipeTransactionRequest.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + snipeTransactionRequest.getRoute().trim() +TILDA + snipeTransactionRequest.getToAddress().toLowerCase().trim()); // id should -> publickey ~ router ~ contractaddresss
		if(approveTransaction != null && approveTransaction.getApprovedHash() != null) {
			if(StringUtils.isBlank(approveTransaction.getStatus())) {
				Optional<TransactionReceipt> transactionRecieptOptional  = orderPreprosorChecks.checkTransactionHashSuccess(approveTransaction.getApprovedHash(), snipeTransactionRequest.getRoute());
				if(transactionRecieptOptional.isPresent()) {
					if(StringUtils.equalsIgnoreCase(transactionRecieptOptional.get().getStatus(), _0X0)) {
						approveTransactionRepository.delete(approveTransaction);
						snipeTransactionRequest.setErrorMessage("APPROVE FAILED");
						snipeOrderHistoryRepository.save(snipeTransactionRequest);
						//snipeOrderRepository.delete(snipeTransactionRequest);
					}else {
						approveTransaction.setStatus(_0X1);
						approveTransactionRepository.update(approveTransaction);
					}
				}
				
			}else {
				return true;
			}
		}else {
			String hash =preApproveProcosser.approve(snipeTransactionRequest.getId(), snipeTransactionRequest.getRoute(), snipeTransactionRequest.getCredentials(), snipeTransactionRequest.getToAddress(), strategyGasProvider, GasModeEnum.fromValue(snipeTransactionRequest.getGasMode()), snipeTransactionRequest.getGasPrice(), snipeTransactionRequest.getGasLimit());
			if(StringUtils.isNotBlank(hash)) {
				ApproveTransaction approveTrnx = new ApproveTransaction();
				approveTrnx.setId(snipeTransactionRequest.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + snipeTransactionRequest.getRoute().trim() +TILDA + snipeTransactionRequest.getToAddress().toLowerCase().trim());
				approveTrnx.setContractAddressInteracted(snipeTransactionRequest.getToAddress().trim());
				approveTrnx.setPublicKey(snipeTransactionRequest.getWalletInfo().getPublicKey().trim());
				approveTrnx.setApprovedHash(hash);
				snipeOrderHistoryRepository.updateApprovedHash(snipeTransactionRequest, hash);
				approveTransactionRepository.insert(approveTrnx);
			}
		}
		return false;
	}
}
