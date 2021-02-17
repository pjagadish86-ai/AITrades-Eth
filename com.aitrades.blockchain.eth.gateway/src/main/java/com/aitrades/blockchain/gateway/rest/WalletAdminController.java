package com.aitrades.blockchain.gateway.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.gateway.domain.Wallet;
import com.aitrades.blockchain.gateway.service.WalletCreator;

@RestController
@RequestMapping("/api")
public class WalletAdminController {

	@Autowired
	private WalletCreator walletCreator;
	
	@PostMapping("/password")
	public String createNewWalletPassword(@RequestBody Wallet wallet) throws Exception {
		return walletCreator.encryptPassword(wallet);
	}
	
	@PostMapping("/newWallet")
	public Wallet createNewWallet(@RequestBody Wallet wallet) throws Exception {
		return walletCreator.createNewWallet(wallet);
	}
	
	@PostMapping("/importWallet")
	public Wallet importExistingWallet(@RequestBody Wallet wallet) throws Exception {
		return walletCreator.createNewWallet(wallet);
	}
	
}
