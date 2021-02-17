package com.aitrades.blockchain.gateway.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.WalletUtils;

import com.aitrades.blockchain.gateway.domain.Wallet;
import com.aitrades.blockchain.gateway.repository.WalletRepository;
import com.aitrades.blockchain.gateway.security.EncryptionSecurityUtil;

@Service
public class WalletCreator {

	@Autowired
	private WalletRepository walletRepository;
	
	public String encryptPassword(Wallet wallet) throws Exception {
		String encryptedPassword = EncryptionSecurityUtil.encrypt(wallet.getPassword());
		wallet.setEncryptedPassword(encryptedPassword);
		return walletRepository.insert(wallet)
							   .block()
							   .getId();
	}
	
	public Wallet createNewWallet(Wallet wallet) throws Exception {
		//String encryptedPassword = EncryptionSecurityUtil.encrypt(wallet.getPassword());
		File walletFileLocation = new File(System.getProperty("user.home", "walletKeyStore"));
		walletFileLocation.mkdir();
		Bip39Wallet bip38Waller = WalletUtils.generateBip39Wallet(wallet.getPassword(), walletFileLocation);
		bip38Waller.getFilename();
		bip38Waller.getMnemonic();
		wallet = new Wallet(bip38Waller.getMnemonic(), "", "", "");
		wallet.setFilePath(walletFileLocation.getPath()+"/"+bip38Waller.getFilename());
		return wallet;
	}
	
	
}
