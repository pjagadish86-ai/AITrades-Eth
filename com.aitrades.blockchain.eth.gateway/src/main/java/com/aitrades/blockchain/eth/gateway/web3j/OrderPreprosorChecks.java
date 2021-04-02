package com.aitrades.blockchain.eth.gateway.web3j;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.domain.Ticker;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;

import io.reactivex.schedulers.Schedulers;

@Component
public class OrderPreprosorChecks {
	
	@Autowired
	public Web3jServiceClientFactory  web3jServiceClientFactory;
	
	@Autowired
	private EthereumDexContractPairData ethereumDexContractPairData;
	
	public Optional<TransactionReceipt> checkStatusOfApprovalTransaction(String approvedHash, String route) {
		return web3jServiceClientFactory.getWeb3jMap().get(route)
										.getWeb3j()
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
			pairAddress = ethereumDexContractPairData.getPair(order.getOrderEntity().getOrderSide().equalsIgnoreCase("BUY") ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress(), 
													          TradeConstants.WETH_MAP.get(order.getRoute()), 
													          order.getRoute().toUpperCase())
													  .parallelStream()
													  .findFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pairAddress.isPresent()
				&& StringUtils.startsWithIgnoreCase((String) pairAddress.get().getValue(), "0x000000")) {
			try {
				pairAddress = ethereumDexContractPairData.getPair(order.getOrderEntity().getOrderSide().equalsIgnoreCase("BUY") ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress(), 
														          TradeConstants.ETH, 
														          order.getRoute().toUpperCase())
														  .parallelStream()
														  .findFirst();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (pairAddress.isPresent()
				&& !StringUtils.startsWithIgnoreCase((String) pairAddress.get().getValue(), "0x000000")) {
			PairData pairData = new PairData();
			Ticker ticker = new Ticker();
			ticker.setAddress((String) pairAddress.get().getValue());
			pairData.setPairAddress(ticker);
			System.out.println("PAIR DATA-"+(String) pairAddress.get().getValue());
			return pairData;
		}
		return null;
	}
}
