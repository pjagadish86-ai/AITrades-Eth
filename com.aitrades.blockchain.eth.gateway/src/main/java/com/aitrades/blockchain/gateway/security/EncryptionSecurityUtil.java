package com.aitrades.blockchain.gateway.security;

import com.google.common.io.BaseEncoding;


public class EncryptionSecurityUtil {
	
    public static String encrypt(String str) throws Exception {
		return  BaseEncoding.base64().encode(str.getBytes("UTF-8"));
	}
    
    public static String decrypt(String decrypt64Str) throws Exception {
    	byte[] contentInBytes = BaseEncoding.base64().decode(decrypt64Str);
    	return new String(contentInBytes, "UTF-8");
    }
    
}
