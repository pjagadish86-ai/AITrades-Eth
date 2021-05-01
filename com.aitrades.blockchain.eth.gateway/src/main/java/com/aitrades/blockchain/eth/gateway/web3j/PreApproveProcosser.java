package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Collections;

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

import com.aitrades.blockchain.eth.gateway.domain.GasModeEnum;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.DexContractStaticCodeValuesService;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
import com.google.common.collect.Lists;

@Service
public class PreApproveProcosser {

 	@Autowired
	private DexContractStaticCodeValuesService dexContractStaticCodeValuesService;
 
    @Autowired
    private Web3jServiceClientFactory  web3jServiceClientFactory;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String approve(String id, String route, Credentials credentials, String contractAddress, StrategyGasProvider customGasProvider,
			  			  GasModeEnum gasModeEnum, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
		FastRawTransactionManager fastRawTxMgr = new FastRawTransactionManager(web3jServiceClientFactory.getWeb3jMap(route).getWeb3j(), 
																			   credentials,
																			   new NoOpProcessor(web3jServiceClientFactory.getWeb3jMap(route).getWeb3j()));
		String data = FunctionEncoder.encode(new Function(TradeConstants.FUNC_APPROVE, 
		         Lists.newArrayList(new Address(dexContractStaticCodeValuesService.getDexContractAddress(route, TradeConstants.FACTORY)), new Uint256(TradeConstants.MAX_UINT256)),
					 Collections.emptyList()));
		BigInteger gasLmt = gasModeEnum.name().equalsIgnoreCase(TradeConstants.CUSTOM) ? gasLimit : BigInteger.valueOf(21000l).add(BigInteger.valueOf(68l).multiply(BigInteger.valueOf(data.getBytes().length)));
		EthSendTransaction ethSendTransaction = aprrov(route, contractAddress, customGasProvider, gasModeEnum, fastRawTxMgr, gasPrice, gasLmt, data);
		if(ethSendTransaction.hasError()) {
			logger.error("Approved failed for id={}, route={}, contractAddress={}, gasPrice={}, gasLimit={}, exceptionmesge={}", id, route, contractAddress, gasPrice, gasLimit,ethSendTransaction.getError().getMessage() );
			throw new Exception(ethSendTransaction.getError().getMessage());
		}
		return ethSendTransaction.getTransactionHash();
	}

	private EthSendTransaction aprrov(String route, String contractAddress, StrategyGasProvider customGasProvider,
									  GasModeEnum gasModeEnum, FastRawTransactionManager fastRawTxMgr, BigInteger gasPrice, 
									  BigInteger gasLmt, String data) throws Exception {
		return fastRawTxMgr.sendTransaction(gasModeEnum.name().equalsIgnoreCase(TradeConstants.CUSTOM) ? gasPrice : customGasProvider.getGasPrice(gasModeEnum), 
											gasLmt, 
											contractAddress, 
											data, 
											BigInteger.ZERO);
	}
}
