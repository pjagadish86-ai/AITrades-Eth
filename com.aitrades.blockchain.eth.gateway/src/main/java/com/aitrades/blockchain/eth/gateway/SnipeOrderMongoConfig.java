package com.aitrades.blockchain.eth.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class SnipeOrderMongoConfig extends AbstractReactiveMongoConfiguration {
	
	private static final String SNIPE_ORDER = "snipeOrder";
	
	@Autowired
	public MongoClient mongoClient;
	
	@Override
	protected String getDatabaseName() {
		return SNIPE_ORDER;
	}

	@Bean(name = "snipeOrderReactiveMongoTemplate")
	public ReactiveMongoTemplate reactiveOrderMongoTemplate(MongoClient mongoClient) {
		return new ReactiveMongoTemplate(mongoClient, getDatabaseName());
	}

	
}