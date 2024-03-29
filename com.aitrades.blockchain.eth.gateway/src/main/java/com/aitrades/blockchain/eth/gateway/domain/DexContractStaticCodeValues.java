package com.aitrades.blockchain.eth.gateway.domain;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)	
@JsonInclude(Include.NON_NULL)
public class DexContractStaticCodeValues {
	
	private List<DexContractStaticCodeValue> dexContractStaticCodeValues;
	private List<AdditionalProperty> additionalProperties;
	
	public List<DexContractStaticCodeValue> getDexContractStaticCodeValues() {
		return dexContractStaticCodeValues;
	}

	public void setDexContractStaticCodeValues(List<DexContractStaticCodeValue> dexContractStaticCodeValues) {
		this.dexContractStaticCodeValues = dexContractStaticCodeValues;
	}

	public List<AdditionalProperty> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(List<AdditionalProperty> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
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
