package com.johanwedin.urlshortener.api;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UrlMappingDTO {
    private String urlId;

    private String originalUrl;

    public UrlMappingDTO() {
        // Jackson deserialization
    }

    public UrlMappingDTO(String urlId, String originalUrl) {
        this.urlId = urlId;
        this.originalUrl = originalUrl;
    }

    @JsonProperty
    public String getUrlId() {
        return urlId;
    }

    @JsonProperty
    public String getOriginalUrl() {
        return originalUrl;
    }
}
