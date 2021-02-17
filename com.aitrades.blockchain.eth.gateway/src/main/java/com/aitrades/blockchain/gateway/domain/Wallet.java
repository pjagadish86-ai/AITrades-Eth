package com.aitrades.blockchain.gateway.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
//infura:https://infura.io/dashboard/ethereum/29f9565415d04f5786c43db50734d3bb/settings

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Wallet {
	
	@Id
	private String id;
	private String mimicWord;
	private String password;
	private String publicKey;
	private String privateKey;
	private String filePath;
	
	@JsonIgnore
	private String encryptedPassword;
	
	@JsonCreator
	public Wallet(@JsonProperty("mimicWord") String mimicWord, 
				  @JsonProperty("password") String password, 
				  @JsonProperty("publicKey") String publicKey, 
				  @JsonProperty("privateKey") String privateKey) {
		this.mimicWord = mimicWord;
		this.password = password;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}
	
	public String getMimicWord() {
		return mimicWord;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPublicKey() {
		return publicKey;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}

	public String getId() {
		return id;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
