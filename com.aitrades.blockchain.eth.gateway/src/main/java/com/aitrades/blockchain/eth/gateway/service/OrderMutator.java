package com.aitrades.blockchain.eth.gateway.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.web3j.OrderPreprosorChecks;
import com.aitrades.blockchain.eth.gateway.web3j.PreApproveProcosser;
import com.aitrades.blockchain.eth.gateway.web3j.StrategyGasProvider;

@Service
public class OrderMutator {
	
	@Autowired
	private OrderProcessor orderProcessor;

	@Autowired
	private PreApproveProcosser preApproveProcosser;
	
	@Autowired
	private StrategyGasProvider strategyGasProvider;
	
	@Autowired
	private OrderPreprosorChecks orderPreposerCheckerAndUpdater;
	
	public String createOrder(Order order) throws Exception {
		if(order.getApprovedHash() == null && StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderSide(), OrderSide.BUY.name())) {
			String approvedHash = approveAndSaveOrder(order);
			if (StringUtils.isNotBlank(approvedHash)) {
				order.setApprovedHash(approvedHash);
			} else {
				throw new Exception("Unable to approve transaction please retry again !!!");
			} 
		}
		
		PairData pairData  = orderPreposerCheckerAndUpdater.getPairData(order);
		if(pairData != null) {
			order.setPairData(pairData);
			return orderProcessor.createOrder(order);
		}
		throw new Exception("PAIR NOT FOUND");
	//	return orderProcessor.createOrder(order);
	}
	
	public String approveAndSaveOrder(Order order) throws Exception {
		return preApproveProcosser.approve(order.getRoute(),
										   order.getCredentials(), 
										   order.getTo().getTicker().getAddress(), 
										   strategyGasProvider, 
										   GasModeEnum.fromValue(order.getGasMode()));
	}

}