package com.aitrades.blockchain.eth.gateway.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistoryRequest;

@Component
public class OrderHistoryValidator {

	public RestExceptionMessage validateOrderHistoryRequest(OrderHistoryRequest orderHistoryRequest) {
		if(orderHistoryRequest == null 
				|| (StringUtils.isBlank(orderHistoryRequest.getEthWalletPublicKey()) &&  
						StringUtils.isBlank(orderHistoryRequest.getBscWalletPublicKey()))) {
			return new RestExceptionMessage(orderHistoryRequest.getId(), "ETH or BSC public key required for history to pull");
		}
		return null;
		
	}
}
