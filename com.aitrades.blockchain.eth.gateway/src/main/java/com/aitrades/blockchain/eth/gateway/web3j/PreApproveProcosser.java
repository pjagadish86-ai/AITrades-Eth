package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Service
public class PreApproveProcosser {

	private static final String CUSTOM = "CUSTOM";
	private static final String PANCAKE = "PANCAKE";
	private static final String UNISWAP = "UNISWAP";
	private static final String SUSHI = "SUSHI";
	private static final String FUNC_APPROVE = "approve";
	private static final String UNISWAP_ROUTER_ADDRESS = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
	private static final String SUSHI_ROUTER_ADDRESS = "0xd9e1cE17f2641f24aE83637ab66a2cca9C378B9F";

	
    private static BigInteger MAX_UINT256 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    private static final Function UNISWAP_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
    															         Lists.newArrayList(new Address(UNISWAP_ROUTER_ADDRESS), new Uint256(MAX_UINT256)),
			  															 Collections.emptyList());
    
    private static final Function SUSHI_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
																       Lists.newArrayList(new Address(SUSHI_ROUTER_ADDRESS), new Uint256(MAX_UINT256)),
																	   Collections.emptyList());
    
    private static final Function PANCAKE_APPROVE_FUNCTION = new Function(FUNC_APPROVE, 
																         Lists.newArrayList(new Address(TradeConstants.PANCAKE_ROUTER_ADDRESS), 
																        		 			new Uint256(MAX_UINT256)),				 
																         Collections.emptyList());
    
    private static final Map<String, String> FUNCTION_ENCODE_ROUTE_MAP = ImmutableMap.of(UNISWAP, FunctionEncoder.encode(UNISWAP_APPROVE_FUNCTION), 
    																					SUSHI,   FunctionEncoder.encode(SUSHI_APPROVE_FUNCTION), 
    																					PANCAKE, FunctionEncoder.encode(PANCAKE_APPROVE_FUNCTION));
    
    
    @Autowired
    private Web3jServiceClientFactory  web3jServiceClientFactory;

	@Resource(name = "pollingTransactionReceiptProcessor")
	private PollingTransactionReceiptProcessor pollingTransactionReceiptProcessor;
	
	@Resource(name= "noOpProcessor")
	private NoOpProcessor noOpProcessor;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String approve(String id, String route, Credentials credentials, String contractAddress, StrategyGasProvider customGasProvider,
			  			  GasModeEnum gasModeEnum, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
		FastRawTransactionManager fastRawTxMgr = new FastRawTransactionManager(web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j(), 
																			   credentials,
																			   noOpProcessor);
		BigInteger gasLmt = gasModeEnum.name().equalsIgnoreCase(CUSTOM) ? gasLimit : BigInteger.valueOf(21000l).add(BigInteger.valueOf(68l).multiply(BigInteger.valueOf(FUNCTION_ENCODE_ROUTE_MAP.get(route).getBytes().length)));
		EthSendTransaction ethSendTransaction = aprrov(route, contractAddress, customGasProvider, gasModeEnum, fastRawTxMgr, gasPrice, gasLmt);
		if(ethSendTransaction.hasError()) {
			logger.error("Approved failed for id={}, route={}, contractAddress={}, gasPrice={}, gasLimit={}, exceptionmesge={}", id, route, contractAddress, gasPrice, gasLimit,ethSendTransaction.getError().getMessage() );
			throw new Exception(ethSendTransaction.getError().getMessage());
		}
		return ethSendTransaction.getTransactionHash();
	}

	private EthSendTransaction aprrov(String route, String contractAddress, StrategyGasProvider customGasProvider,
									  GasModeEnum gasModeEnum, FastRawTransactionManager fastRawTxMgr, BigInteger gasPrice, 
									  BigInteger gasLmt) throws Exception {

		return fastRawTxMgr.sendTransaction(gasModeEnum.name().equalsIgnoreCase(CUSTOM) ? gasPrice : customGasProvider.getGasPrice(gasModeEnum), 
											gasLmt, 
											contractAddress, 
											FUNCTION_ENCODE_ROUTE_MAP.get(route), 
											BigInteger.ZERO);
	}
}
