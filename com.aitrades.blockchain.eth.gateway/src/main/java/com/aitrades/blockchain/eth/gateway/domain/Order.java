package com.aitrades.blockchain.eth.gateway.domain;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.web3j.crypto.Credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonIgnoreProperties(ignoreUnknown = true)	
@JsonInclude(Include.NON_NULL)
public class Order {

	@Id
	private String id;
	private String route;
	private WalletInfo walletInfo;
	private TickerEntity from;
	private TickerEntity to;
	private String gasMode;
	private Gas gasPrice;
	private Gas gasLimit;
	private Slipage slippage;
	private PairData pairData;
	private OrderEntity orderEntity;
	private EventState eventState;
	private List<AdditionalProperty> additionalProperty;
	private Integer orderCode;
	private String approvedHash;
	private String read;
	private String errorMessage;
	private boolean approveStatusCheck;
	private boolean isFee;
	private String parentSnipeId;
	private String autoSnipeLimitSellTrailPercent;
	private AuditInformation auditInformation;
	private String publicKey;
	private String swappedHash;
	private BigDecimal executionPrice;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	
	public WalletInfo getWalletInfo() {
		return walletInfo;
	}
	public void setWalletInfo(WalletInfo walletInfo) {
		this.walletInfo = walletInfo;
	}
	public TickerEntity getFrom() {
		return from;
	}
	public void setFrom(TickerEntity from) {
		this.from = from;
	}
	public TickerEntity getTo() {
		return to;
	}
	public void setTo(TickerEntity to) {
		this.to = to;
	}
	public Gas getGasPrice() {
		return gasPrice;
	}
	public void setGasPrice(Gas gasPrice) {
		this.gasPrice = gasPrice;
	}
	public Gas getGasLimit() {
		return gasLimit;
	}
	public void setGasLimit(Gas gasLimit) {
		this.gasLimit = gasLimit;
	}

	public Slipage getSlippage() {
		return slippage;
	}
	public void setSlippage(Slipage slippage) {
		this.slippage = slippage;
	}
	public PairData getPairData() {
		return pairData;
	}
	public void setPairData(PairData pairData) {
		this.pairData = pairData;
	}
	public OrderEntity getOrderEntity() {
		return orderEntity;
	}
	public void setOrderEntity(OrderEntity orderEntity) {
		this.orderEntity = orderEntity;
	}
	public EventState getEventState() {
		return eventState;
	}
	public void setEventState(EventState eventState) {
		this.eventState = eventState;
	}
	public List<AdditionalProperty> getAdditionalProperty() {
		return additionalProperty;
	}
	public void setAdditionalProperty(List<AdditionalProperty> additionalProperty) {
		this.additionalProperty = additionalProperty;
	}
	public Integer getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}
	
	public String getGasMode() {
		return gasMode;
	}
	public void setGasMode(String gasMode) {
		this.gasMode = gasMode;
	}
	
	public String getApprovedHash() {
		return approvedHash;
	}
	public void setApprovedHash(String approvedHash) {
		this.approvedHash = approvedHash;
	}
	
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public boolean isFee() {
		return isFee;
	}
	public void setFee(boolean isFee) {
		this.isFee = isFee;
	}
	
	public boolean isApproveStatusCheck() {
		return approveStatusCheck;
	}
	public void setApproveStatusCheck(boolean approveStatusCheck) {
		this.approveStatusCheck = approveStatusCheck;
	}
	
	public String getParentSnipeId() {
		return parentSnipeId;
	}
	public void setParentSnipeId(String parentSnipeId) {
		this.parentSnipeId = parentSnipeId;
	}
	
	public String getAutoSnipeLimitSellTrailPercent() {
		return autoSnipeLimitSellTrailPercent;
	}
	public void setAutoSnipeLimitSellTrailPercent(String autoSnipeLimitSellTrailPercent) {
		this.autoSnipeLimitSellTrailPercent = autoSnipeLimitSellTrailPercent;
	}
	
	public AuditInformation getAuditInformation() {
		return auditInformation;
	}
	public void setAuditInformation(AuditInformation auditInformation) {
		this.auditInformation = auditInformation;
	}
	public String getPublicKey() {
		return getWalletInfo().getPublicKey();
	}
	public String getSwappedHash() {
		return swappedHash;
	}
	public void setSwappedHash(String swappedHash) {
		this.swappedHash = swappedHash;
	}
	
	public BigDecimal getExecutionPrice() {
		return executionPrice;
	}
	public void setExecutionPrice(BigDecimal executionPrice) {
		this.executionPrice = executionPrice;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order)) {
            return false;
        }
        Order order = (Order) obj;
        return new EqualsBuilder()
                .append(getId(), order.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }
    
	@JsonIgnore
	public Credentials getCredentials() {
		return Credentials.create(getWalletInfo().getPrivateKey());
	}
}
