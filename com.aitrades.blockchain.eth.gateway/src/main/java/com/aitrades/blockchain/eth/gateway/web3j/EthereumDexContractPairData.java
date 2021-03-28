package com.aitrades.blockchain.eth.gateway.web3j;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import com.aitrades.blockchain.eth.gateway.Web3jServiceClient;
import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;

public class EthereumDexContractPairData {

    public static final String FUNC_GETPAIR = "getPair";
    
	@Resource(name = "web3jServiceClient")
	private Web3jServiceClient web3jServiceClient;
	
	
	public List<Type> getPair(String tokenA, String tokenB, String route) throws Exception{
		final Function function = new Function(FUNC_GETPAIR, Arrays.asList(new Address(tokenA), new Address(tokenB)),
											   Arrays.asList(new TypeReference<Address>() {
											}));
		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(TradeConstants.FACTORY_MAP.get(route), TradeConstants.FACTORY_MAP.get(route), data);
		EthCall ethCall = web3jServiceClient.getWeb3j()
										    .ethCall(transaction, DefaultBlockParameterName.LATEST)
										    .flowable()
										    .blockingSingle();
		if(ethCall.hasError()) {
			throw new Exception(ethCall.getError().getMessage());
		}
		
		String value = ethCall.getValue();
		
		return FunctionReturnDecoder.decode(value, function.getOutputParameters());
	}
}
