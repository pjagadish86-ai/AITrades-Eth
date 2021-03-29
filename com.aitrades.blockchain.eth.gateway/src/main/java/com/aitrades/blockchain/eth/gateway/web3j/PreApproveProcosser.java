package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import com.aitrades.blockchain.eth.gateway.Web3jServiceClient;
import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import io.reactivex.schedulers.Schedulers;

@Service
public class PreApproveProcosser {

	private static final String PANCAKE = "PANCAKE";
	public static final String UNISWAP = "UNISWAP";
	public static final String FUNC_APPROVE = "approve";
	public static final String UNISWAP_FACTORY_ADDRESS = "0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f";
	public static final String UNISWAP_ROUTER_ADDRESS = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
	public static final Map<String, String> ROUTER_MAP = ImmutableMap.of(UNISWAP, UNISWAP_ROUTER_ADDRESS);
	
    public static BigInteger MAX_UINT256 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    @Autowired
	public Web3jServiceClientFactory  web3jServiceClientFactory;
	
	public String approve(String route, Credentials credentials, String contractAddress, StrategyGasProvider customGasProvider,
			  			  GasModeEnum gasModeEnum) throws Exception {
		final Function approveFunction = new Function(FUNC_APPROVE,
												  Lists.newArrayList(new Address(UNISWAP_ROUTER_ADDRESS), new Uint256(MAX_UINT256)),
												  Collections.emptyList());
		
		String data = FunctionEncoder.encode(approveFunction);
		
		EthGetTransactionCount ethGetTransactionCountFlowable = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
																			  .ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING)
																			  .flowable()
																			  .subscribeOn(Schedulers.io())
																			  .blockingSingle();
		
		BigInteger gasLimit = null;
		BigInteger gasPrice = null;
		if(StringUtils.equalsIgnoreCase(route, PANCAKE)) {
			gasPrice = customGasProvider.getGasPricePancake(gasModeEnum);
			gasLimit = customGasProvider.getGasLimitOfPancake(true);
		}else {
			gasPrice =  customGasProvider.getGasPrice(gasModeEnum);
			gasLimit =	 customGasProvider.getGasLimit(route, true);
		}
		if(StringUtils.equalsIgnoreCase("local", "local")) {
			gasPrice =  customGasProvider.getGasPrice();
			gasLimit =	 customGasProvider.getGasLimit();
		}
		RawTransaction rawTransaction = RawTransaction.createTransaction(ethGetTransactionCountFlowable.getTransactionCount(), 
																		 gasPrice,
																	     gasLimit, 
																	     contractAddress, 
																	     BigInteger.ZERO, 
																	     data);
		
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		
		EthSendTransaction ethSendTransaction = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
														      .ethSendRawTransaction(Numeric.toHexString(signedMessage))
														      .flowable()
														      .blockingSingle();
		
		if(ethSendTransaction.hasError()) {
			throw new Exception(ethSendTransaction.getError().getMessage());
			}
		return ethSendTransaction.getTransactionHash();
	}
}
