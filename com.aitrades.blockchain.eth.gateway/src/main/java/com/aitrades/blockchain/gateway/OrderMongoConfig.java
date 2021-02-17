package com.aitrades.blockchain.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class OrderMongoConfig extends AbstractReactiveMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "order";
	}

	@Bean
	public ReactiveMongoTemplate reactiveOrderMongoTemplate(MongoClient mongoClient) {
		return new ReactiveMongoTemplate(mongoClient, getDatabaseName());
	}
	
}