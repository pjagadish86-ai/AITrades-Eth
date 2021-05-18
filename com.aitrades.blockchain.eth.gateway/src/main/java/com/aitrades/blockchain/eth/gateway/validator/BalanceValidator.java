package com.aitrades.blockchain.eth.gateway.validator;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.OrderSide;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.DexContractStaticCodeValuesService;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

@Component
public class BalanceValidator {

	private static final String BALANCE_NOT_GOOD = "Insuffcient  Token Balance!!!";
	private static final String ACCOUNT_BALANCE_NOT_GOOD = "Insuffcient Account Balance specified for gas calculation, please lower gas settings!!!";
	
	@Autowired
	private DexContractStaticCodeValuesService dexContractStaticCodeValuesService;
	
	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;
	
	public RestExceptionMessage validateBalance(Order order) throws Exception {
		if(StringUtils.isBlank(order.getParentSnipeId())) {// if we have parentsnipeorderid that mean we are auto snipe sell enabled here, so we never have balance here, so we should skip this.
			// for virgin orders we either buy or sell we should be good
			boolean hasValidBal = hasTokenValidBalance(order);
			if(!hasValidBal) {
				return new RestExceptionMessage(order.getId(), BALANCE_NOT_GOOD);
			}
			 double gasPriceInEither = Double.valueOf(Convert.fromWei(order.getGasPrice().getValue(), Convert.Unit.ETHER).toPlainString());
			boolean hasValidAccountBal  = hasValidAccountBalance(order.getRoute(), order.getPublicKey(),
					gasPriceInEither, 
					order.getGasLimit().getValueBigInteger());
			if(!hasValidAccountBal) {
				//return new RestExceptionMessage(order.getId(), ACCOUNT_BALANCE_NOT_GOOD);
			}
		}
		return null;
	}
	
	public RestExceptionMessage validateNativeCoinBalance(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		boolean nativeCoinBalance = orderProcessorPrechecker.getNativeCoinBalance(snipeTransactionRequest.getPublicKey(), snipeTransactionRequest.getInputTokenValueAmountAsBigInteger(), snipeTransactionRequest.getRoute());
		if(!nativeCoinBalance) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
		}
		return null;
	}
	
	
	private boolean hasTokenValidBalance(Order order) {
		try {
			String contractAddress = order.getOrderEntity().getOrderSide().equalsIgnoreCase(OrderSide.BUY.name()) ? dexContractStaticCodeValuesService.getDexContractAddress(order.getRoute(), TradeConstants.WNATIVE) : order.getFrom().getTicker().getAddress();
			return orderProcessorPrechecker.getBalanceUsingWrapper(order.getRoute(), order.getFrom().getAmountAsBigInteger(), contractAddress, order.getPublicKey(), order.getCredentials());
		} catch (Exception e) {
		}
		return false;
	}

	public boolean hasValidAccountBalance(String route, String publickey, double gasPriceInEither, BigInteger gasLimit) {
		try {
			BigInteger accountBalance = orderProcessorPrechecker.getAccountBalance(route, publickey);
			double transactionFee = gasPriceInEither * gasLimit.doubleValue();
			return true;//accountBalance >= BigDecimal.valueOf(transactionFee).doubleValue();
		} catch (Exception e) {
		}
		return false;
	}
	
	//String id, BigDecimal inputAmount, String publicKey, String address, String route, String decimals
	public RestExceptionMessage validateSnipeTokenBalance(SnipeTransactionRequest snipeTransactionRequest) throws Exception {
		boolean tokenBalance = orderProcessorPrechecker.getBalanceUsingWrapper(snipeTransactionRequest.getRoute(), snipeTransactionRequest.getInputTokenValueAmountAsBigInteger(),  dexContractStaticCodeValuesService.getDexContractAddress(snipeTransactionRequest.getRoute(), TradeConstants.WNATIVE), snipeTransactionRequest.getPublicKey(), snipeTransactionRequest.getCredentials());
		if(!tokenBalance) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), BALANCE_NOT_GOOD);
		}
		 double gasPriceInEither = Double.valueOf(Convert.fromWei(snipeTransactionRequest.getGasPriceStr(), Convert.Unit.ETHER).toPlainString());
		 boolean hasValidAccountBal  = hasValidAccountBalance(snipeTransactionRequest.getRoute(), 
															 snipeTransactionRequest.getPublicKey(), 
															 gasPriceInEither, 
															 snipeTransactionRequest.getGasLimit());
		if(!hasValidAccountBal) {
			return new RestExceptionMessage(snipeTransactionRequest.getId(), ACCOUNT_BALANCE_NOT_GOOD);
		}
		return null;
	}
}
