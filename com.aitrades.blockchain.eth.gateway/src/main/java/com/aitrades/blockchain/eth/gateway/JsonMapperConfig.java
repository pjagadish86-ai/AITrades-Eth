package com.aitrades.blockchain.eth.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aitrades.blockchain.eth.gateway.domain.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Configuration
public class JsonMapperConfig {

	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	
	@Bean(name= "orderObjectReader")
	public ObjectReader orderObjectReader() {
		return objectMapper().readerFor(Order.class);
	}
}
