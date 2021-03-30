package com.aitrades.blockchain.eth.gateway.web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Service
public class PreApproveProcosser {

	public static final String PANCAKE = "PANCAKE";
	public static final String UNISWAP = "UNISWAP";
	public static final String SUSHI = "SUSHI";
	public static final String FUNC_APPROVE = "approve";
	public static final String UNISWAP_FACTORY_ADDRESS = "0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f";
	public static final String UNISWAP_ROUTER_ADDRESS = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
	public static final Map<String, String> ROUTER_MAP = ImmutableMap.of(UNISWAP, UNISWAP_ROUTER_ADDRESS);
	
    public static BigInteger MAX_UINT256 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    public static final Function UNISWAP_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
    															         Lists.newArrayList(new Address(UNISWAP_ROUTER_ADDRESS), new Uint256(MAX_UINT256)),
			  															 Collections.emptyList());
    
    public static final Function SUSHI_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
																       Lists.newArrayList(new Address(UNISWAP_ROUTER_ADDRESS), new Uint256(MAX_UINT256)),
																	   Collections.emptyList());
    
    public static final Function PANCAKE_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
																         Lists.newArrayList(new Address(UNISWAP_ROUTER_ADDRESS), 
																        		 			new Uint256(MAX_UINT256)),				 
																         Collections.emptyList());
    
    public static final Map<String, Function> APPROVE_ROUTE_FUNCTION_MAP = ImmutableMap.of(UNISWAP, UNISWAP_APPROVE_FUNCTION, SUSHI, SUSHI_APPROVE_FUNCTION, PANCAKE, PANCAKE_APPROVE_FUNCTION);
    
    public static final Map<String, String> FUNCTION_ENCODE_ROUTE_MAP = ImmutableMap.of(UNISWAP, FunctionEncoder.encode(UNISWAP_APPROVE_FUNCTION), 
    																					SUSHI,   FunctionEncoder.encode(SUSHI_APPROVE_FUNCTION), 
    																					PANCAKE, FunctionEncoder.encode(PANCAKE_APPROVE_FUNCTION));
    
    @Autowired
    private Web3jServiceClientFactory  web3jServiceClientFactory;

	@Resource(name = "pollingTransactionReceiptProcessor")
	private PollingTransactionReceiptProcessor pollingTransactionReceiptProcessor;
	
	@Resource(name= "noOpProcessor")
	private NoOpProcessor noOpProcessor;
	
	public String approve(String route, Credentials credentials, String contractAddress, StrategyGasProvider customGasProvider,
			  			  GasModeEnum gasModeEnum) throws Exception {
		FastRawTransactionManager fastRawTxMgr = new FastRawTransactionManager(web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j(), 
																			   credentials,
																			   noOpProcessor);
		
		EthSendTransaction ethSendTransaction = extracted(route, contractAddress, customGasProvider, gasModeEnum, fastRawTxMgr);
		if(ethSendTransaction.hasError()) {
			throw new Exception(ethSendTransaction.getError().getMessage());
		}
		return ethSendTransaction.getTransactionHash();
	}

	@Async
	private EthSendTransaction extracted(String route, String contractAddress, StrategyGasProvider customGasProvider,
										 GasModeEnum gasModeEnum, FastRawTransactionManager fastRawTxMgr) throws Exception {
		return fastRawTxMgr.sendTransaction(customGasProvider.getGasPrice(gasModeEnum), 
											customGasProvider.getGasLimit(), 
											contractAddress, 
											FUNCTION_ENCODE_ROUTE_MAP.get(route), 
											BigInteger.ZERO);
	}
}
