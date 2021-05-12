package com.aitrades.blockchain.eth.gateway.orderhistory.transformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistories;
import com.aitrades.blockchain.eth.gateway.domain.orderhistory.OrderHistory;
import com.aitrades.blockchain.eth.gateway.service.ApproveProcessor;
import com.aitrades.blockchain.eth.gateway.service.OrderHistoryDataFetcher;

@Service
public class HistoryTransformer {

	private static final String SNIPE2 = "SNIPE";
	private static final String WBNB = "WBNB";
	private static final String WETH = "WETH";
	private static final String SELL = "SELL";
	private static final String _0 = "0";
	private static final String E = "E";
	private static final String BUY = "BUY";
	private static final String PANCAKE = "PANCAKE";
	@Autowired
	private OrderHistoryDataFetcher web3jDataFetcher;

	@Autowired
	private ApproveProcessor approveProcessor;
	
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
		if(snipe.getRoute().equalsIgnoreCase(PANCAKE)) {
			if(snipe.getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol(WBNB) ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbolSnipe(snipe, snipe.getToAddress())) ;
			}else {
				history.setToTickerSymbol(WBNB) ;
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbolSnipe(snipe, snipe.getFromAddress())) ;
			}
		}else {
			if(snipe.getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol(WETH) ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbolSnipe(snipe, snipe.getToAddress())) ;

			}else {
				history.setToTickerSymbol(WETH) ;
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbolSnipe(snipe, snipe.getFromAddress())) ;
			}
		}
		history.setToTickerAddress(snipe.getToAddress());
		history.setInput(snipe.getInputTokenValueAmountAsBigDecimal().toString()) ;
		history.setExecutedprice(web3jDataFetcher.getExecutedPrice(snipe)) ;
		String snipeApprovedHash = web3jDataFetcher.getSnipeApprovedHash(snipe);
		history.setApprovedhash(snipeApprovedHash) ;
		String apporvedHash = web3jDataFetcher.transactionHashStatus(snipeApprovedHash, snipe.getRoute());
		history.setApprovedhashStatus(apporvedHash) ;
		history.setSwappedhash(snipe.getSwappedHash()) ;
		Tuple2<String, BigInteger> tuple = web3jDataFetcher.getSnipeSwappedHashStatus(snipe);
		String swapStatus = tuple.component1();
		//ApproveProcessor
		if(!StringUtils.equalsIgnoreCase(apporvedHash, "SUCCESS") && StringUtils.equalsIgnoreCase("SUCCESS", swapStatus)) {
			approveProcessor.approve(snipe.getId());
		}
		
		history.setSwappedhashStatus(swapStatus) ;
		history.setOrderstate(snipe.getSnipeStatus()) ;
		String balance = web3jDataFetcher.getBalanceAtBlock(snipe, snipe.getToAddress(), tuple.component2());
		history.setOutput(StringUtils.contains(balance, E) ? _0: balance) ;
		
		history.setOrderside(SNIPE2) ;
		history.setErrormessage(snipe.getErrorMessage()) ;
		history.setGasLimit(snipe.getGasLimit().toString());
		history.setGasPrice(Convert.fromWei(snipe.getGasPrice().toString(), Convert.Unit.GWEI).toString());
		history.setSlipage(snipe.getSlipage().multiply(new BigDecimal(100)).toString());
		return history;
	}

	private OrderHistory mapOrderToHistory(Order order) throws Exception {
		OrderHistory history = new OrderHistory();
		
		history.setOrderId(order.getId());
		history.setRoute(order.getRoute());
		history.setTradetype(order.getOrderEntity().getOrderType()) ;
		if(order.getRoute().equalsIgnoreCase("3")) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol(WBNB) ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;
			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(SELL)){
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
				history.setToTickerSymbol(WBNB) ;
			}
		}else if(order.getRoute().equalsIgnoreCase("1") || order.getRoute().equalsIgnoreCase("2")) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol(WETH) ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;

			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(SELL)){
				history.setToTickerSymbol(WETH) ;
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
			}
		}
		else if(order.getRoute().equalsIgnoreCase("9") || order.getRoute().equalsIgnoreCase("9")) {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol(WETH) ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;

			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(SELL)){
				history.setToTickerSymbol(WETH) ;
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
			}
		}else {
			if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY)) {
				history.setFromTickerSymbol("WFTM") ;
				history.setToTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getTo().getTicker().getAddress())) ;

			}else if(order.getOrderEntity().getOrderSide().equalsIgnoreCase(SELL)){
				history.setToTickerSymbol("WFTM") ;
				history.setFromTickerSymbol(web3jDataFetcher.getTickerSymbol(order, order.getFrom().getTicker().getAddress())) ;
			}
		}
		//history.setToTickerAddress(order.getTo().getTicker().getAddress());
		history.setInput(order.getFrom().getAmount()) ;
		
 		String balance = web3jDataFetcher.getBalance(order, order.getTo().getTicker().getAddress());
		history.setOutput(StringUtils.contains(balance, E) ? _0: balance) ;
		history.setExecutedprice(web3jDataFetcher.getExecutedPrice(order)) ;
		String swapStatus = web3jDataFetcher.transactionHashStatus(history.getSwappedhash(), order.getRoute());
		history.setOrderstate(StringUtils.isBlank(swapStatus) ? order.getOrderEntity().getOrderState() : swapStatus) ;
		history.setApprovedhash(web3jDataFetcher.getApprovedHash(order)) ;
		history.setApprovedhashStatus(web3jDataFetcher.getApprovedHashStatus(order)) ;
		history.setSwappedhash(web3jDataFetcher.getSwappedHash(order)) ;
		history.setSwappedhashStatus(swapStatus) ;
		history.setOrderside(order.getOrderEntity().getOrderSide()) ;
		history.setErrormessage(web3jDataFetcher.getErrorMessage(order)) ;
		history.setGasLimit(order.getGasLimit().getValue());
		history.setGasPrice(Convert.fromWei(order.getGasPrice().getValue().toString(), Convert.Unit.GWEI).toString());
		history.setSlipage(order.getSlippage().getSlipagePercent());
		return history;
	}

}
