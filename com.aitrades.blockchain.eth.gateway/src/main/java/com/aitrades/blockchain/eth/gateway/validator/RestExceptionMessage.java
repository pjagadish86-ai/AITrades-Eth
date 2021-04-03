package com.aitrades.blockchain.eth.gateway.validator;

public class RestExceptionMessage {

	private String id;
	private String errorCode;
	private String errorMessage;
	private String detailedErrorMessage;
	
	public RestExceptionMessage(String id, String errorMessage) {
		super();
		this.id = id;
		this.errorMessage = errorMessage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getDetailedErrorMessage() {
		return detailedErrorMessage;
	}
	public void setDetailedErrorMessage(String detailedErrorMessage) {
		this.detailedErrorMessage = detailedErrorMessage;
	}
	
}
