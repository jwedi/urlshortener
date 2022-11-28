package com.johanwedin.urlshortener.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateShortenedUrlResponse extends ResponseBase {
    private UrlMappingDTO urlMapping;

    public CreateShortenedUrlResponse(String traceId, UrlMappingDTO urlMapping) {
        super(traceId);
        this.urlMapping = urlMapping;
    }

    @JsonProperty
    public UrlMappingDTO getUrlMapping() {
        return urlMapping;
    }
}
