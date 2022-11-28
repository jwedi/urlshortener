package com.johanwedin.urlshortener.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateShortenedUrlRequest {
    private String originalUrl;

    public CreateShortenedUrlRequest() {
    }

    @JsonProperty
    public String getOriginalUrl() {
        return originalUrl;
    }
}
