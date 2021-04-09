package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.PairData;
import com.aitrades.blockchain.eth.gateway.domain.TradeOverview;
import com.aitrades.blockchain.eth.gateway.mq.RabbitMQCreateOrderSender;
import com.aitrades.blockchain.eth.gateway.repository.OrderHistoryRepository;
import com.aitrades.blockchain.eth.gateway.repository.OrderRepository;
import com.aitrades.blockchain.eth.gateway.repository.TradeOverviewRepository;
import com.aitrades.blockchain.eth.gateway.service.ApprovedTransactionProcessor;
import com.aitrades.blockchain.eth.gateway.service.TakeProfitOrderStrategy;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

public class OrderGatewayEndpoint {

	private static final String LOCK = "LOCK";

	@Autowired
	private RabbitMQCreateOrderSender rabbitMQCreateOrderSender;

	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;
	
	@Autowired
	private ApprovedTransactionProcessor approvedTransactionProcessor;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;
	
	@Autowired
	private TradeOverviewRepository tradeOverviewRepository;
	
	@Autowired
	private TakeProfitOrderStrategy takeProfitOrderStrategy;
	
	private static final String _0X1 = "0x1";
	
	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) throws Exception {
		List<Order> uniqueOrders = new ArrayList<>(new LinkedHashSet<>(orders));
		for(Order order : uniqueOrders) {
			try {
				if (hasApprovedTransaction(order)) {
					if (order.getPairData() == null) {
						PairData pairData = populatePairData(order);
						if (pairData != null) {
							order.setPairData(pairData);
						}
					}
					
					verifyOrderEligibleToSendToQueue(order);
				}
			} catch (Exception e) {
				order.setErrorMessage(e.getMessage());
				orderHistoryRepository.save(order);
				orderRepository.delete(order);
				e.printStackTrace();
			}
		}
		return orders;
	}

	private void verifyOrderEligibleToSendToQueue(Order order) throws Exception {
		if (order.getPairData() != null) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.SELL.name())) { 
				if(StringUtils.isBlank(order.getParentSnipeId())) {// this is virgin sell order
					boolean hasValidBalanceToSell = orderProcessorPrechecker.getBalance(order);
					if(hasValidBalanceToSell) {
						sendToQueueAndUpdateLock(order);
					}
				}else{// this is take profit or auto matic sniper sell order
						TradeOverview tradeOverview = tradeOverviewRepository.findById(order.getParentSnipeId());
						if(tradeOverview != null) {
							if(StringUtils.isNotBlank(tradeOverview.getErrorMessage())) {// for any reason exception in snipe, delete the sniper sell order
								order.setErrorMessage(tradeOverview.getErrorMessage());
								orderHistoryRepository.save(order);
								orderRepository.delete(order);
								return;
							}
							// if u are here, then snipe is success, now check swapped hash transaction success
							Optional<TransactionReceipt> transactionReceiptOptional = orderProcessorPrechecker.checkTransactionHashSuccess(tradeOverview.getSwappedHash(), tradeOverview.getRoute());
							if(transactionReceiptOptional.isPresent()) {
								if(StringUtils.equalsIgnoreCase(transactionReceiptOptional.get().getStatus(), _0X1)) {
									Order orderRes  = takeProfitOrderStrategy.buildSellOrderStrategy(order, tradeOverview);
									sendToQueueAndUpdateLock(orderRes);// now we need mutate sell order strategy here.
								}
							}
						}
				}
			} else {
				sendToQueueAndUpdateLock(order);// this is buy
			}
		}
	}

	private boolean hasApprovedTransaction(Order order) throws Exception {
		return order.isApproveStatusCheck() ? approvedTransactionProcessor.checkAndProcessBuyApproveTransaction(order) : true;
	}
	
	private PairData populatePairData(Order order) {
		return orderProcessorPrechecker.getPairData(order);
	}
	
	private void sendToQueueAndUpdateLock(Order order) {
		order.setRead(LOCK);
		orderRepository.save(order);
		rabbitMQCreateOrderSender.send(order);
	}
}
