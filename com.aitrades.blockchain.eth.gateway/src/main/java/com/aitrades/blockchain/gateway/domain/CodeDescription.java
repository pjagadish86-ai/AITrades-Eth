package com.aitrades.blockchain.gateway.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CodeDescription {

    private String code;
    private String description;
    private String longDescription;

    @JsonCreator
    public CodeDescription(@JsonProperty("code") String code,
            			   @JsonProperty("description") String description,
            			   @JsonProperty("longDescription") String longDescription) {
        this.code = code;
        this.description = description;
        this.longDescription = longDescription;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
