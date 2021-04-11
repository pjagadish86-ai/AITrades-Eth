package com.aitrades.blockchain.eth.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistories;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistoryRequest;
import com.aitrades.blockchain.eth.gateway.orderhistory.transformer.HistoryTransformer;
import com.aitrades.blockchain.eth.gateway.repository.OrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.SnipeOrderRepository;

@Service
public class OrderHistoriesRetriever {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;
	
	@Autowired
	private SnipeOrderHistoryRepository snipeOrderHistoryRepository;
	
	@Autowired
	private SnipeOrderRepository snipeOrderRepository;
	
	@Autowired
	private HistoryTransformer historyTransformer;
	
	
	public OrderHistories fetchOrderHistories(OrderHistoryRequest orderHistoryRequest) throws Exception {
		List<Order> orders = orderRepository.fetchOrdersById(orderHistoryRequest.getWalletIds());
		
		if(CollectionUtils.isEmpty(orders)) {
			orders = new ArrayList<>();
		}
		orders.addAll(orderHistoryRepository.fetchOrdersById(orderHistoryRequest.getWalletIds()));
		
		List<SnipeTransactionRequest> snipes = snipeOrderRepository.fetchOrdersById(orderHistoryRequest.getWalletIds());
		if(CollectionUtils.isEmpty(snipes)) {
			snipes = new ArrayList<>();
		}
		snipes.addAll(snipeOrderHistoryRepository.fetchOrdersById(orderHistoryRequest.getWalletIds()));
		return historyTransformer.transformToHistories(orders, snipes);		
	}

}
