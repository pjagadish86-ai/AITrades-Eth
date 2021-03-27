package com.aitrades.blockchain.eth.gateway.web3j;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.Web3jServiceClient;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;

import io.reactivex.schedulers.Schedulers;

@Component
public class ApprovedTransactionStatusChecker {
	
	@Resource(name = "web3jServiceClient")
	private Web3jServiceClient web3jServiceClient;
	
	
	public Optional<TransactionReceipt> checkStatusOfApprovalTransaction(String approvedHash) {
		return web3jServiceClient.getWeb3j()
							     .ethGetTransactionReceipt(approvedHash)
							     .flowable()
							     .subscribeOn(Schedulers.io())
							     .blockingSingle()
							     .getTransactionReceipt()
							     .filter(e -> e.getStatus() != "0x0");
	};
}
