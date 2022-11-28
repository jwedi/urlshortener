package com.johanwedin.urlshortener.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResponseBase {
    private String error;
    private String traceId;

    public ResponseBase(String error, String traceId) {
        this.error = error;
        this.traceId = traceId;
    }

    public ResponseBase(String traceId) {
        this.traceId = traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String getError() {
        return error;
    }

    @JsonProperty
    public String getTraceId() {
        return traceId;
    }
}
