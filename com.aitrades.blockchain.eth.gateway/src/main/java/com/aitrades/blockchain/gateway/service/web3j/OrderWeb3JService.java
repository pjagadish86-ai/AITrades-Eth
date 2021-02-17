package com.aitrades.blockchain.gateway.service.web3j;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Service;
import org.web3j.contracts.token.ERC20Interface;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import io.reactivex.Flowable;

@Service
public class OrderWeb3JService<R, T> implements ERC20Interface<R, T>{

	@Override
	public RemoteCall<BigInteger> totalSupply() {
		return null;
	}

	@Override
	public RemoteCall<BigInteger> balanceOf(String who) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteCall<TransactionReceipt> transfer(String to, BigInteger value) {
		return null;
	}

	@Override
	public List<T> getTransferEvents(TransactionReceipt transactionReceipt) {
		return null;
	}

	@Override
	public Flowable<T> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
		return null;
	}

	@Override
	public RemoteCall<BigInteger> allowance(String owner, String spender) {
		return null;
	}

	@Override
	public RemoteCall<TransactionReceipt> approve(String spender, BigInteger value) {
		return null;
	}

	@Override
	public RemoteCall<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
		return null;
	}

	@Override
	public List<R> getApprovalEvents(TransactionReceipt transactionReceipt) {
		return null;
	}

	@Override
	public Flowable<R> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
		return null;
	}

}
