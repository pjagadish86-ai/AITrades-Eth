package com.aitrades.blockchain.eth.gateway.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Type;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.ApproveTransaction;
import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.aitrades.blockchain.eth.gateway.domain.SnipeTransactionRequest;
import com.aitrades.blockchain.eth.gateway.repository.ApproveTransactionRepository;
import com.aitrades.blockchain.eth.gateway.repository.TradeOverviewRepository;
import com.aitrades.blockchain.eth.gateway.web3j.EthereumDexContract;
import com.aitrades.blockchain.eth.gateway.web3j.OrderProcessorPrechecker;

import io.reactivex.schedulers.Schedulers;

@Service
public class OrderHistoryDataFetcher {
	
	private static final String FILLED = "FILLED";

	private static final String WORKING = "WORKING";

	private static final String STRING = "";

	private static final String SUCCESS = "SUCCESS";

	private static final String FAILED = "FAILED";

	@Autowired
	private Web3jServiceClientFactory web3jServiceClientFactory;
	
	@Autowired
	private EthereumDexContract ethereumDexContract;
	
	@Autowired
	private TradeOverviewRepository tradeOverviewRepository;
	
	@Autowired
	private OrderProcessorPrechecker orderProcessorPrechecker;

	@Autowired
	private ApproveTransactionRepository approveTransactionRepository;
	
	private static final String TILDA = "~";
	
	private static final String _0X0 = "0x0";
	private static final String BUY = "BUY";
	
	public String getTickerSymbol(Order order, String address) {
		ERC20 erc20Contract = ERC20.load(address, web3jServiceClientFactory.getWeb3jMap().get(order.getRoute()).getWeb3j(), order.getCredentials(), new DefaultGasProvider());
		try {
			return erc20Contract.symbol().flowable().subscribeOn(Schedulers.io()).blockingFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	@SuppressWarnings("rawtypes")
	public String getBalance(Order order, String address) throws Exception {
		List<Type> types =  ethereumDexContract.getBalance(order.getId(), order.getPublicKey(), address, order.getRoute());
		 if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
			for(Type type : types) {
				BigInteger balanceAsBigInt = (BigInteger)type.getValue();
				return Convert.fromWei(balanceAsBigInt.toString(), Convert.Unit.ETHER).toString();
			}
		 }
		 return null;
	}
	
	@SuppressWarnings("rawtypes")
	public String getBalanceAtBlock(SnipeTransactionRequest request, String address, BigInteger blockNbr) throws Exception {
		try {
			List<Type> types =  ethereumDexContract.getBalance(request.getId(), request.getPublicKey(), address, request.getRoute(), blockNbr);
			 if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
				for(Type type : types) {
					BigInteger balanceAsBigInt = (BigInteger)type.getValue();
					return Convert.fromWei(balanceAsBigInt.toString(), Convert.Unit.ETHER).toString();
				}
			 }
		} catch (Exception e) {
		}
		 return null;
	}

	public String getExecutedPrice(Order order) {
		try {
			if(order.getOrderEntity().getOrderState().equalsIgnoreCase(FILLED)) {
				return tradeOverviewRepository.findById(order.getId()).getExecutedPrice().toString();
			}
		} catch (Exception e) {
		}
		return STRING;
	}

	public String getApprovedHash(Order order) {
		String address  = order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY) ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress();
		ApproveTransaction approveTransaction = approveTransactionRepository.find(order.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + order.getRoute().trim() +TILDA + address.toLowerCase().trim()); // id should -> publickey ~ router ~ contractaddresss
		return approveTransaction.getApprovedHash();
	}

	public String getApprovedHashStatus(Order order) {
		String address  = order.getOrderEntity().getOrderSide().equalsIgnoreCase(BUY) ? order.getTo().getTicker().getAddress() : order.getFrom().getTicker().getAddress();
		ApproveTransaction approveTransaction = approveTransactionRepository.find(order.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + order.getRoute().trim() +TILDA + address.toLowerCase().trim()); // id should -> publickey ~ router ~ contractaddresss
        Optional<TransactionReceipt> transactionRecieptOptional =orderProcessorPrechecker.checkTransactionHashSuccess(approveTransaction.getApprovedHash(), order.getRoute());
		
        if(transactionRecieptOptional.isPresent()) {
			if(StringUtils.equalsIgnoreCase(transactionRecieptOptional.get().getStatus(), _0X0)) {
				return FAILED;
			}else {
				return SUCCESS;
			}
		}
		return STRING;
	}

	public String getSwappedHash(Order order) {
		return order.getSwappedHash();
	}

	public Tuple2<String, BigInteger> getSnipeSwappedHashStatus(SnipeTransactionRequest snipeTransactionRequest) {
		 Optional<TransactionReceipt> transactionRecieptOptional =orderProcessorPrechecker.checkTransactionHashSuccess(snipeTransactionRequest.getSwappedHash(), snipeTransactionRequest.getRoute());
		String status = STRING;	
		BigInteger blockNbr = null;
		 if(transactionRecieptOptional.isPresent()) {
				if(StringUtils.equalsIgnoreCase(transactionRecieptOptional.get().getStatus(), _0X0)) {
					status= FAILED;
				}else {
					status= SUCCESS;
				}
				blockNbr = transactionRecieptOptional.get().getBlockNumber();
			}
			return new Tuple2<>(status, blockNbr);
	}

	public String getErrorMessage(Order order) {
		return order.getErrorMessage();
	}

	public String getTickerSymbolSnipe(SnipeTransactionRequest snipe, String toAddress) {
		ERC20 erc20Contract = ERC20.load(snipe.getToAddress(), web3jServiceClientFactory.getWeb3jMap().get(snipe.getRoute()).getWeb3j(), snipe.getCredentials(), new DefaultGasProvider());
		try {
			return erc20Contract.symbol().flowable().subscribeOn(Schedulers.io()).blockingFirst();
		} catch (Exception e) {
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public String getBalance(SnipeTransactionRequest snipe, String toAddress) throws Exception {
		//String id, String owner, String contractAddress, String route
		List<Type> types =  ethereumDexContract.getBalance(snipe.getId(), snipe.getPublicKey(), toAddress, snipe.getRoute());
		 if(CollectionUtils.isNotEmpty(types) && types.get(0) != null) {
			for(Type type : types) {
				BigInteger balanceAsBigInt = (BigInteger)type.getValue();
				return Convert.fromWei(balanceAsBigInt.toString(), Convert.Unit.ETHER).toString();
			}
		 }
		 return null;
	}

	public String getExecutedPrice(SnipeTransactionRequest snipe) {
		try {
			if(snipe.getSnipeStatus().equalsIgnoreCase(FILLED)) {
				return tradeOverviewRepository.findById(snipe.getId()).getExecutedPrice().toString();
			}
		} catch (Exception e) {
		}
		return STRING;
	}

	public String transactionHashStatus(String approvedHash, String route) {
		 Optional<TransactionReceipt> transactionRecieptOptional =orderProcessorPrechecker.checkTransactionHashSuccess(approvedHash, route);
			if(transactionRecieptOptional.isPresent()) {
				if(StringUtils.equalsIgnoreCase(transactionRecieptOptional.get().getStatus(), _0X0)) {
					return FAILED;
				}else {
					return SUCCESS;
				}
			}
			return STRING;
	}

	public String getSnipeApprovedHash(SnipeTransactionRequest snipeTransactionRequest) {
		ApproveTransaction approveTransaction = approveTransactionRepository.find(snipeTransactionRequest.getWalletInfo().getPublicKey().toLowerCase().trim() + TILDA + snipeTransactionRequest.getRoute().trim() +TILDA + snipeTransactionRequest.getToAddress().toLowerCase().trim()); // id should -> publickey ~ router ~ contractaddresss
		return approveTransaction.getApprovedHash();
	}

}
