package com.aitrades.blockchain.eth.gateway.rest;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.eth.gateway.service.PriceFeedOracleRetriever;

@RestController
@RequestMapping("/price/api/v1")
public class PriceFeedOracleController {

	@Autowired
	private PriceFeedOracleRetriever priceFeedOracleRetriever;
	
	@GetMapping("price/{route}/{ticker}")
	public BigDecimal getOraclePriceFeed(@PathVariable("route") String route, @PathVariable("ticker") String ticker) throws Exception {
		return priceFeedOracleRetriever.retrievePriceOracle(route, ticker);
		
	}
}
