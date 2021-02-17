package com.aitrades.blockchain.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class WalletInfoMongoConfig extends AbstractReactiveMongoConfiguration {

	@Bean(name = "walletMongoConfigTemplate")
	public ReactiveMongoTemplate reactiveWalletMongoTemplate(final MongoClient client) {
		return new ReactiveMongoTemplate(client, getDatabaseName());
	}

	@Override
	protected String getDatabaseName() {
		return "wallet";
	}
	
}