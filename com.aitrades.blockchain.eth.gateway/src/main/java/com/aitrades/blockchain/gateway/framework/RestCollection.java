package com.aitrades.blockchain.gateway.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestCollection<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6443141905409828191L;
	
	private List<T> content = new ArrayList<T>();
	private final String message;

	@JsonCreator
	public RestCollection(@JsonProperty("content") List<T> content, @JsonProperty("message") String message) {
		this.content = content;
		this.message = message;
	}

	public List<T> getContent() {
		return content;
	}

	public String getMessage() {
		return message;
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
