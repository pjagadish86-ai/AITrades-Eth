package com.aitrades.blockchain.eth.gateway.web3j;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.aitrades.blockchain.eth.gateway.service.Web3jServiceClientFactory;
@SuppressWarnings("rawtypes")
@Service
public class EthereumDexContract {

	private static final String FUNC_GETPAIR = "getPair";
    private static final String FUNC_BALANCEOF = "balanceOf";
    
	@Autowired
	private Web3jServiceClientFactory web3jServiceClientFactory;
	
	public List<Type> getPair(String tokenA, String tokenB, String route) throws Exception{
		final Function function = new Function(FUNC_GETPAIR, Arrays.asList(new Address(tokenA), new Address(tokenB)),
											   Arrays.asList(new TypeReference<Address>() {
											}));
		Transaction transaction = Transaction.createEthCallTransaction(TradeConstants.FACTORY_MAP.get(route), TradeConstants.FACTORY_MAP.get(route), FunctionEncoder.encode(function));
		EthCall ethCall = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
															     .ethCall(transaction, DefaultBlockParameterName.LATEST)
															     .flowable()
															     .blockingSingle();
		if(ethCall.hasError()) {
			throw new Exception(ethCall.getError().getMessage());
		}
		return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
	}
	
	public List<Type> getBalance(String owner, String contractAddress, String route) throws Exception{
		final Function function = new Function(FUNC_BALANCEOF, 
								               Arrays.asList(new Address(owner)), 
								               Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
		Transaction transaction = Transaction.createEthCallTransaction(owner, contractAddress, FunctionEncoder.encode(function));
		EthCall ethCall = web3jServiceClientFactory.getWeb3jMap().get(route).getWeb3j()
																	        .ethCall(transaction, DefaultBlockParameterName.LATEST)
																	        .flowable()
																	        .blockingSingle();
		if(ethCall.hasError()) {
			throw new Exception(ethCall.getError().getMessage());
		}
		return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
	}
}
