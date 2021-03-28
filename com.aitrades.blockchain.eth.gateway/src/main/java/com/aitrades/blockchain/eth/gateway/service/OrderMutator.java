package com.aitrades.blockchain.eth.gateway.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
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
	
	public String createOrder(Order order) throws Exception {
		if(StringUtils.equalsIgnoreCase(order.getOrderEntity().getOrderSide(), OrderSide.BUY.name())) {
			String approvedHash = approveAndSaveOrder(order);
			if (StringUtils.isNotBlank(approvedHash)) {
				order.setApprovedHash(approvedHash);
			} else {
				throw new Exception("Unable to approve transaction please retry again !!!");
			} 
		}
		return orderProcessor.createOrder(order);
	}
	
	public String approveAndSaveOrder(Order order) throws Exception {
		return preApproveProcosser.approve(order.getCredentials(), 
										   order.getTo().getTicker().getAddress(), 
										   strategyGasProvider, 
										   GasModeEnum.fromValue(order.getGasMode()));
	}

}
