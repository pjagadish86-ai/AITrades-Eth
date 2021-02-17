package com.aitrades.blockchain.gateway.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aitrades.blockchain.gateway.domain.Wallet;

import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {

	Mono<Wallet> insert(final Wallet wallet);
	
}

