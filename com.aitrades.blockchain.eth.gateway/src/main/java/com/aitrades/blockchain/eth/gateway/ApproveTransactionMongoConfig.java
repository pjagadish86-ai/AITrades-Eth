package com.aitrades.blockchain.eth.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class ApproveTransactionMongoConfig extends AbstractReactiveMongoConfiguration {
	
	private static final String APPROVE_TRANSACTION_MONGO = "approveTransactionMongo";
	
	@Autowired
	public MongoClient mongoClient;
	
	@Override
	protected String getDatabaseName() {
		return APPROVE_TRANSACTION_MONGO;
	}

	@Bean(name = "approveTransactionMongoTemplate")
	public ReactiveMongoTemplate approveTransactionMongo(MongoClient mongoClient) {
		return new ReactiveMongoTemplate(mongoClient, getDatabaseName());
	}

	
}