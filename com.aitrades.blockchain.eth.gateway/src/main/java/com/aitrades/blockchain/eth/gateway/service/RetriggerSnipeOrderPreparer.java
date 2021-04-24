package com.aitrades.blockchain.eth.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.common.UUIDGenerator;
import com.aitrades.blockchain.eth.gateway.domain.RetriggerSnipeOrder;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
@Component
public class RetriggerSnipeOrderPreparer {
	
	private static final String AVAL = "AVAL";
	private static final String WORKING = "WORKING";
	
	@Autowired
	public RetriggerSnipeOrderProcessor retriggerOrderProcessor;

	public RetriggerSnipeOrder retriggerOrder(RetriggerSnipeOrder retriggerSnipeOrder) {
		SnipeTransactionRequest snipeOrder = retriggerOrderProcessor.fetchParentSnipeOrderById(retriggerSnipeOrder.getParentSnipeOrderId());
		System.out.println("Parent snipe orderid-> "+ retriggerSnipeOrder.getParentSnipeOrderId());
		if(snipeOrder != null) {
			String id = UUIDGenerator.nextHex(UUIDGenerator.TYPE1);
			System.out.println("Retriggered new order id-> "+ id);
			snipeOrder.setId(id);
			snipeOrder.setSnipe(false);
			snipeOrder.setSnipeStatus(WORKING);
			snipeOrder.setRead(AVAL);
			snipeOrder.setErrorMessage(null);
			if(retriggerSnipeOrder.getSlipage() != null) {
				snipeOrder.setSlipage(retriggerSnipeOrder.getSlipage());
			}
			if(retriggerSnipeOrder.getSlipageInDouble() != null) {
				snipeOrder.setSlipageInDouble(retriggerSnipeOrder.getSlipageInDouble());
			}
			if(retriggerSnipeOrder.getGasLimit() != null) {
				snipeOrder.setGasLimit(retriggerSnipeOrder.getGasLimit());
			}
			if(retriggerSnipeOrder.getGasPrice() != null) {
				snipeOrder.setGasPrice(retriggerSnipeOrder.getGasPrice());
			}
			String saveId = retriggerOrderProcessor.saveSnipeOrder(snipeOrder).getId();
			retriggerSnipeOrder.setSnipeOrderId(saveId);
			return retriggerSnipeOrder;
		}
		return null;
	}

}
