package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
@SuppressWarnings("rawtypes")
@Service
public class EthereumDexContract {

	private static final String FUNC_GETPAIR = "getPair";
    private static final String FUNC_BALANCEOF = "balanceOf";
    private static final String FUNC_SYMBOL = "symbol";

	private final Logger logger = LoggerFactory.getLogger(getClass());
    
	@Autowired 
	private Web3jServiceClientFactory web3jServiceClientFactory;
	
	public List<Type> getPair(String tokenA, String tokenB, String route, String id) throws Exception{
		final Function function = new Function(FUNC_GETPAIR, Arrays.asList(new Address(tokenA), new Address(tokenB)),
											   Arrays.asList(new TypeReference<Address>() {
											}));
		Transaction transaction = Transaction.createEthCallTransaction(TradeConstants.FACTORY_MAP.get(route), TradeConstants.FACTORY_MAP.get(route), FunctionEncoder.encode(function));
		EthCall ethCall = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
															     .ethCall(transaction, DefaultBlockParameterName.LATEST)
															     .flowable()
															     .blockingSingle();
		if(ethCall.hasError()) {
			logger.error("exception occurred for getPair order/snipe id ={}, tokenA={}, tokenB={}, route={}, exception={}", id, tokenA, tokenB, route, ethCall.getError().getMessage());
			throw new Exception(ethCall.getError().getMessage());
		}
		return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
	}
	
	public List<Type> getBalance(String id, String owner, String contractAddress, String route, BigInteger blockNbr) throws Exception{
		final Function function = new Function(FUNC_BALANCEOF, 
	               Arrays.asList(new Address(owner)), 
	               Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
		Transaction transaction = Transaction.createEthCallTransaction(owner, contractAddress, FunctionEncoder.encode(function));
		EthCall ethCall = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
												        .ethCall(transaction, new DefaultBlockParameterNumber(blockNbr))
												        .flowable()
												        .blockingSingle();
		if(ethCall.hasError()) {
			logger.error("exception occurred for getBalance order/snipe id ={}, owner={}, contractAddress={}, route={}, exception={}", id, owner, contractAddress, route, ethCall.getError().getMessage());
			throw new Exception(ethCall.getError().getMessage());
		}
		return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
	}
	public List<Type> getBalance(String id, String owner, String contractAddress, String route) throws Exception{
		final Function function = new Function(FUNC_BALANCEOF, 
								               Arrays.asList(new Address(owner)), 
								               Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
		Transaction transaction = Transaction.createEthCallTransaction(owner, contractAddress, FunctionEncoder.encode(function));
		EthCall ethCall = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
																	        .ethCall(transaction, DefaultBlockParameterName.LATEST)
																	        .flowable()
																	        .blockingSingle();
		if(ethCall.hasError()) {
			logger.error("exception occurred for getBalance order/snipe id ={}, owner={}, contractAddress={}, route={}, exception={}", id, owner, contractAddress, route, ethCall.getError().getMessage());
			throw new Exception(ethCall.getError().getMessage());
		}
		return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
	}
	
	public List<Type> getSymbol(){
		final Function function = new Function(FUNC_SYMBOL,  Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
		return null;
	}
	
	public BigInteger getDecimals(String addresss, String route, Credentials credentials){
		EthereumDexContractReserves ethereumDexContractReserves = new EthereumDexContractReserves(addresss, web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j(), credentials);
		return ethereumDexContractReserves.decimals().flowable().blockingFirst();
	}
	
	
}
