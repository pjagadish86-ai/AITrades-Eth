package com.aitrades.blockchain.eth.gateway.orderhistory.transformer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistories;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistory;
import com.aitrades.blockchain.eth.gateway.service.OrderHistoryDataFetcher;

@Service
public class HistoryTransformer {

	@Autowired
	private OrderHistoryDataFetcher orderHistoryDataFetcher;

	public OrderHistories transformToHistories(List<Order> orders, List<SnipeTransactionRequest> snipes) throws Exception {
		OrderHistories orderHistories = new OrderHistories();
		List<OrderHistory> histories = new ArrayList<>();
		for(Order order : orders) {
			histories.add(mapOrderToHistory(order));
		}
		for(SnipeTransactionRequest snipe : snipes) {
			histories.add(mapSnipeToHistory(snipe));
		}
		orderHistories.setOrderHistories(histories);
		return orderHistories;
	}

	private OrderHistory mapSnipeToHistory(SnipeTransactionRequest snipe) throws Exception {
		OrderHistory history = new OrderHistory();
		
		history.setOrderId(snipe.getId());
		history.setRoute(snipe.getRoute());
		history.setTradetype(snipe.getOrderType()) ;
		if(snipe.getRoute().equalsIgnoreCase("PANCAKE")) {
			if(snipe.getOrderSide().equalsIgnoreCase("BUY")) {
				history.setFromTickerSymbol("BNB") ;
				history.setToTickerSymbol(orderHistoryDataFetcher.getTickerSymbolSnipe(snipe, snipe.getToAddress())) ;
			}else {
				history.setToTickerSymbol("BNB") ;
				history.setFromTickerSymbol(orderHistoryDataFetcher.getTickerSymbolSnipe(snipe, snipe.getFromAddress())) ;
			}
		}else {
			if(snipe.getOrderSide().equalsIgnoreCase("BUY")) {
				history.setFromTickerSymbol("ETH") ;
				history.setToTickerSymbol(orderHistoryDataFetcher.getTickerSymbolSnipe(snipe, snipe.getToAddress())) ;

			}else {
				history.setToTickerSymbol("ETH") ;
				history.setFromTickerSymbol(orderHistoryDataFetcher.getTickerSymbolSnipe(snipe, snipe.getFromAddress())) ;
			}
		}
		
		history.setInput(snipe.getInputTokenValueAmountAsBigDecimal().toString()) ;
		
		String balance = orderHistoryDataFetcher.getBalance(snipe, snipe.getToAddress());
		history.setOutput(StringUtils.contains(balance, "E") ? "0": balance) ;
		history.setExecutedprice(orderHistoryDataFetcher.getExecutedPrice(snipe)) ;
		history.setOrderstate(snipe.getSnipeStatus()) ;
		String snipeApprovedHash = orderHistoryDataFetcher.getSnipeApprovedHash(snipe);
		history.setApprovedhash(snipeApprovedHash) ;
		history.setApprovedhashStatus(orderHistoryDataFetcher.transactionHashStatus(snipeApprovedHash, snipe.getRoute())) ;
		history.setSwappedhash(snipe.getSwappedHash()) ;
		history.setSwappedhashStatus(orderHistoryDataFetcher.transactionHashStatus(snipe.getSwappedHash(), snipe.getRoute())) ;
		history.setOrderside("SNIPE") ;
		history.setErrormessage(snipe.getErrorMessage()) ;
		return history;
	}

	private OrderHistory mapOrderToHistory(Order order) throws Exception {
		OrderHistory history = new OrderHistory();
		
		history.setOrderId(order.getId());
		history.setRoute(order.getRoute());
		history.setTradetype(order.getOrderEntity().getOrderType()) ;
		if(order.getRoute().equalsIgnoreCase("PANCAKE")) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase("BUY")) {
				history.setFromTickerSymbol("BNB") ;
				history.setToTickerSymbol(orderHistoryDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;
			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase("SELL")){
				history.setFromTickerSymbol(orderHistoryDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
				history.setToTickerSymbol("BNB") ;
			}
		}else {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase("BUY")) {
				history.setFromTickerSymbol("ETH") ;
				history.setToTickerSymbol(orderHistoryDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;

			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase("SELL")){
				history.setToTickerSymbol("ETH") ;
				history.setFromTickerSymbol(orderHistoryDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
			}
		}
		
		history.setInput(order.getFrom().getAmount()) ;
		
 		String balance = orderHistoryDataFetcher.getBalance(order, order.getTo().getTicker().getAddress());
		history.setOutput(StringUtils.contains(balance, "E") ? "0": balance) ;
		history.setExecutedprice(orderHistoryDataFetcher.getExecutedPrice(order)) ;
		history.setOrderstate(order.getOrderEntity().getOrderState()) ;
		history.setApprovedhash(orderHistoryDataFetcher.getApprovedHash(order)) ;
		history.setApprovedhashStatus(orderHistoryDataFetcher.getApprovedHashStatus(order)) ;
		history.setSwappedhash(orderHistoryDataFetcher.getSwappedHash(order)) ;
		history.setSwappedhashStatus(orderHistoryDataFetcher.getSwappedHashStatus(order)) ;
		history.setOrderside(order.getOrderEntity().getOrderSide()) ;
		history.setErrormessage(orderHistoryDataFetcher.getErrorMessage(order)) ;
		return history;
	}

}