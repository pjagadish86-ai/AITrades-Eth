package com.aitrades.blockchain.eth.gateway.web3j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.DefaultGasProvider;
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class EthereumDexContractReserves extends Contract {
    public static final String BIN_NOT_PROVIDED = "Bin file was not provided";
	private static final String FUNC_GETRESERVES = "getReserves";
	public static final String FUNC_DEPOSIT = "deposit"; 
	public static final String FUNC_DECIMALS = "decimals";
	
	public EthereumDexContractReserves(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
		super(BIN_NOT_PROVIDED, contractAddress, web3j, credentials, gasPrice, gasLimit);
	}
	
	public EthereumDexContractReserves(String contractAddress, Web3j web3j, Credentials credentials) {
		super(BIN_NOT_PROVIDED, contractAddress, web3j, credentials, new DefaultGasProvider());
	}

	public RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>> getReserves() {
        final Function function = new Function(FUNC_GETRESERVES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint112>() {}, new TypeReference<Uint112>() {}, new TypeReference<Uint32>() {}));
        return new RemoteFunctionCall<>(function,
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        if(results == null || results.isEmpty()) {
                        	return new Tuple3<>(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
                        }
                        return new Tuple3<>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }
	
	  public RemoteFunctionCall<TransactionReceipt> deposit(BigInteger weiValue) {
	        final Function function = new Function(
	                FUNC_DEPOSIT, 
	                Arrays.<Type>asList(), 
	                Collections.<TypeReference<?>>emptyList());
	        return executeRemoteCallTransaction(function, weiValue);
	  }
	  
    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

}
