package com.johanwedin.urlshortener.models;

import com.google.common.base.Objects;

public class UrlMapping {
    private String urlId;
    private String originalUrl;

    public UrlMapping(String urlId, String originalUrl) {
        this.urlId = urlId;
        this.originalUrl = originalUrl;
    }

    public String getUrlId() {
        return urlId;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMapping that = (UrlMapping) o;
        return Objects.equal(urlId, that.urlId) && Objects.equal(originalUrl, that.originalUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(urlId, originalUrl);
    }
}
