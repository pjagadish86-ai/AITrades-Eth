package com.aitrades.blockchain.eth.gateway.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Convert;

import com.aitrades.blockchain.eth.gateway.domain.TradeConstants;
import com.google.common.collect.Lists;

import io.reactivex.schedulers.Schedulers;

@Service
public class PriceFeedOracleRetriever {
	
	private static final String FUNC_GETAMOUNTIN = "getAmountsIn";
	private static final List<TypeReference<?>> GET_AMTS_IN_OUT_PARAMS = Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {});
	private static final BigInteger USDC_MWEI = Convert.toWei("1", Convert.Unit.MWEI).toBigInteger();
	
	public static Map<String, String> USDC_TOKEN_ADDRESS = new HashMap<>();
	
	static {
		USDC_TOKEN_ADDRESS.put("1", "0xdac17f958d2ee523a2206206994597c13d831ec7");
		USDC_TOKEN_ADDRESS.put("2", "0xdac17f958d2ee523a2206206994597c13d831ec7");
		USDC_TOKEN_ADDRESS.put("3", "0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d");
		USDC_TOKEN_ADDRESS.put("4", "0x04068da6c83afcfa0e13ba15a6696662335d5b75");
		USDC_TOKEN_ADDRESS.put("5", "0x04068da6c83afcfa0e13ba15a6696662335d5b75");
		USDC_TOKEN_ADDRESS.put("6", "0x04068da6c83afcfa0e13ba15a6696662335d5b75");
		USDC_TOKEN_ADDRESS.put("7", "0x04068da6c83afcfa0e13ba15a6696662335d5b75");
		USDC_TOKEN_ADDRESS.put("8", "0x04068da6c83afcfa0e13ba15a6696662335d5b75");//FIXME
	}
	
	public static Map<String, String> WRAPPED_TOKEN_ADDRESS = new HashMap<>();
	
	static {
		WRAPPED_TOKEN_ADDRESS.put("1", "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		WRAPPED_TOKEN_ADDRESS.put("2", "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");
		WRAPPED_TOKEN_ADDRESS.put("3", "0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c");
		WRAPPED_TOKEN_ADDRESS.put("4", "0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		WRAPPED_TOKEN_ADDRESS.put("5", "0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		WRAPPED_TOKEN_ADDRESS.put("6", "0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		WRAPPED_TOKEN_ADDRESS.put("7", "0x21be370d5312f44cb42ce377bc9b8a0cef1a4c83");
		WRAPPED_TOKEN_ADDRESS.put("8", "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2");//FIXME
	}
	
	@Autowired
	private Web3jServiceClientFactory web3jServiceClientFactory;
	
 	@Autowired
	private DexContractStaticCodeValuesService dexContractStaticCodeValuesService;
 
	@SuppressWarnings("rawtypes")
	public BigDecimal retrievePriceOracle(String route, String ticker) throws Exception {

		List<Address> memoryPathAddress = Lists.newArrayList(new Address(ticker), new Address(WRAPPED_TOKEN_ADDRESS.get(route)), new Address(USDC_TOKEN_ADDRESS.get(route)));
		final Function function = new Function(FUNC_GETAMOUNTIN, Arrays.<Type>asList(new Uint256(USDC_MWEI),
											  new DynamicArray<Address>(Address.class, memoryPathAddress)), GET_AMTS_IN_OUT_PARAMS);
		String data = FunctionEncoder.encode(function);
		EthCall resp = web3jServiceClientFactory.getWeb3jMap(route).getWeb3j()
												.ethCall(Transaction.createEthCallTransaction(memoryPathAddress.get(0).getValue(),
														 dexContractStaticCodeValuesService.getDexContractAddress(route, TradeConstants.ROUTER), data),
														 DefaultBlockParameterName.LATEST)
												.flowable()
												.subscribeOn(Schedulers.io())
												.blockingSingle();
		final List<Type> response  = FunctionReturnDecoder.decode(resp.getValue(), function.getOutputParameters());
		final BigInteger amountsOut = (BigInteger)(((DynamicArray<Type>)response.get(0)).getValue().get(0).getValue());
		double rep = USDC_MWEI.doubleValue() / amountsOut.doubleValue();
		return BigDecimal.valueOf(rep).setScale(2, RoundingMode.DOWN);
	}

}
