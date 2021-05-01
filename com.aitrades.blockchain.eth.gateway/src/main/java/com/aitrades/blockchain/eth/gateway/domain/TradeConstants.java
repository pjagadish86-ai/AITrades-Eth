package com.aitrades.blockchain.eth.gateway.domain;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public final class TradeConstants {

	private TradeConstants() {
	}
	public static final String CUSTOM = "CUSTOM";
	public static final String FUNC_APPROVE = "approve";
	 
	public static final String WNATIVE = "WNATIVE";
	public static final String FACTORY = "FACTORY";
	public static final String ROUTER = "ROUTER";
	
    public static final BigInteger MAX_UINT256 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

	public static final String UNISWAP = "UNISWAP";
	public static final String PANCAKE = "PANCAKE";
	public static final String SUSHI = "SUSHI";
	public static final String ORDER_DECISION = "ORDER_DECISION";

	public static final String SNIPETRANSACTIONREQUEST = "SNIPETRANSACTIONREQUEST";
    public static final String SWAP_ETH_FOR_TOKEN_HASH = "SWAP_ETH_FOR_TOKEN_HASH";
	public static final String APPROVE_HASH = "APPROVE_HASH";
	public static final String PAIR_CREATED = "PAIR_CREATED";
	public static final String HAS_RESERVES = "HAS_RESERVES";
	public static final String HAS_LIQUIDTY_EVENT = "HAS_LIQUIDTY_EVENT";
	
	public static final String OUTPUT_TOKENS = "OUTPUT_TOKENS";
	public static final String INPUT_TOKENS = "INPUT_TOKENS";
	
	public static final String ETH = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";

	public static final String APPROVE_HASH_ISAVAILABLE = "APPROVE_HASH_ISAVAILABLE";

	public static final String FILLED = "FILLED";

	public static final String ORDER = "ORDER";

	public static final String SWAP_TOKEN_FOR_ETH_HASH = "SWAP_TOKEN_FOR_ETH_HASH";
	public static final String FTM = "FTM";
	public static Map<String, String> DECIMAL_MAP = null;
	
	static {
		DECIMAL_MAP = new HashMap<String, String>();
		DECIMAL_MAP.put("0", "WEI");
		DECIMAL_MAP.put("3", "KWEI");
		DECIMAL_MAP.put("6", "MWEI");
		DECIMAL_MAP.put("9", "GWEI");
		DECIMAL_MAP.put("12", "SZABO");
		DECIMAL_MAP.put("15", "FINNEY");
		DECIMAL_MAP.put( "18", "ETHER");
		DECIMAL_MAP.put("21", "KETHER");
		DECIMAL_MAP.put("24", "METHER");
		DECIMAL_MAP.put("27", "GETHER");
		
	}
	
}
