package com.aitrades.blockchain.eth.gateway.web3j;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.Web3jServiceClient;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.domain.Ticker;

import io.reactivex.schedulers.Schedulers;

@Component
public class OrderPreprosorChecks {
	
	@Resource(name = "web3jServiceClient")
	private Web3jServiceClient web3jServiceClient;
	
	@Autowired
	private EthereumDexContractPairData ethereumDexContractPairData;
	
	public Optional<TransactionReceipt> checkStatusOfApprovalTransaction(String approvedHash) {
		return web3jServiceClient.getWeb3j()
							     .ethGetTransactionReceipt(approvedHash)
							     .flowable()
							     .subscribeOn(Schedulers.io())
							     .blockingSingle()
							     .getTransactionReceipt()
							     .filter(e -> e.getStatus() != "0x0");
	};
	
	public PairData getPairData(Order order) {
		@SuppressWarnings("rawtypes")
		Optional<Type> pairAddress = Optional.empty();
		try {
			pairAddress = ethereumDexContractPairData.getPair(order.getFrom().getTicker().getAddress(), 
													          order.getTo().getTicker().getAddress(), 
													          order.getRoute().toUpperCase())
													  .parallelStream()
													  .findFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pairAddress.isPresent()
				&& !StringUtils.startsWithIgnoreCase((String) pairAddress.get().getValue(), "0x000000")) {
			PairData pairData = new PairData();
			Ticker ticker = new Ticker();
			ticker.setAddress((String) pairAddress.get().getValue());
			pairData.setPairAddress(ticker);
			return pairData;
		}
		return null;
	}
}
