package com.aitrades.blockchain.eth.gateway.integration.createorder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ServiceActivator(inputChannel = "addNewOrderToRabbitMq")
	public List<Order> addNewOrderToRabbitMq(List<Order> orders) throws Exception {
		logger.info("started processing addNewOrderToRabbitMq");
		List<Order> uniqueOrders = new ArrayList<>(new LinkedHashSet<>(orders));
		for(Order order : uniqueOrders) {
			try {
				if (hasApprovedTransaction(order)) {
					logger.info(" Transaction has been approved for order id={}, approvedhash ={}", order.getId(), order.getApprovedHash());
					if (order.getPairData() == null) {
						PairData pairData = populatePairData(order);
						if (pairData != null) {
							logger.info("Pair data available for orderid={}, pairdata={}", order.getId(), pairData);
							order.setPairData(pairData);
						}else {
							logger.info("Pair data not available for orderid={}", order.getId());
						}
					}
					verifyOrderEligibleToSendToQueue(order);
				}else {
					logger.info(" Transaction hasnot been approved for order id={}, approvedhash ={}", order.getId(), order.getApprovedHash());
				}
			} catch (Exception e) {
				logger.error("Exception occured in addNewOrderToRabbitMq for orderid={}, order={}, execption={}", order.getId(), order, e);
				order.setErrorMessage(e.getMessage());
				orderHistoryRepository.save(order);
				orderRepository.delete(order);
			}
		}
		return orders;
	}

	private void verifyOrderEligibleToSendToQueue(Order order) throws Exception {
		String id = order.getId();
		logger.info(" started processing verifyOrderEligibleToSendToQueue for order id={}", id);
		if (order.getPairData() != null) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.SELL.name())) {
				if(StringUtils.isBlank(order.getParentSnipeId())) {// this is virgin sell order
					logger.info("this is pure virgin sell order id ={}", id);
					boolean hasValidBalanceToSell = orderProcessorPrechecker.getBalance(order);
					logger.info("Has account has balance to sell id={}, hasValidBalanceToSell={}", id, hasValidBalanceToSell);
					if(hasValidBalanceToSell) {
						logger.info("sending message to  process order queue for order id={}", id);
						sendToQueueAndUpdateLock(order);
					}
				}else{// this is take profit or auto matic sniper sell order
						logger.info("started processing take profit order for order id={}, parent snipe order id={}", id, order.getParentSnipeId());
						TradeOverview tradeOverview = tradeOverviewRepository.findById(order.getParentSnipeId());
						logger.info("getting parent sniper order id confirmation tradeoverview order id={}, parent snipe order id={}, tradingOverview={}", id, order.getParentSnipeId(), tradeOverview);
						if(tradeOverview != null) {
							if(StringUtils.isNotBlank(tradeOverview.getErrorMessage())) {// for any reason exception in snipe, delete the sniper sell order
								logger.info("we have exception message from tradeview obj hence deleting the take profit order tradeOverview={}", tradeOverview);
								order.setErrorMessage(tradeOverview.getErrorMessage());
								orderHistoryRepository.save(order);
								orderRepository.delete(order);
								return;
							}
							// if u are here, then snipe is success, now check swapped hash transaction success
							Optional<TransactionReceipt> transactionReceiptOptional = orderProcessorPrechecker.checkTransactionHashSuccess(tradeOverview.getSwappedHash(), tradeOverview.getRoute());
							logger.info("Gettting transaction recietp for snipeorder status", transactionReceiptOptional);
							if(transactionReceiptOptional.isPresent()) {
								if(StringUtils.equalsIgnoreCase(transactionReceiptOptional.get().getStatus(), _0X1)) {
									logger.info("status success for snipe order={}, swappedhash={}", tradeOverview.getId(), tradeOverview.getSwappedHash());
									Order orderRes  = takeProfitOrderStrategy.buildSellOrderStrategy(order, tradeOverview);
									logger.info(" Mutated order and build the trade strategey object order={} and sending to processorderqueue", orderRes);
									sendToQueueAndUpdateLock(orderRes);// now we need mutate sell order strategy here.
									
								}else {
									logger.info("Gettting transaction recietp for snipeorder status", transactionReceiptOptional);
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
		logger.info("started processing sendToQueueAndUpdateLock for order={}", order);
		order.setRead(LOCK);
		orderRepository.save(order);
		rabbitMQCreateOrderSender.send(order);
	}
}
