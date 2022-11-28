package com.johanwedin.urlshortener.db;

import com.johanwedin.urlshortener.models.UrlMapping;

import java.util.Optional;

public interface UrlRepository {
    Optional<UrlMapping> getByUrlId(String urlId);
    UrlMapping addMapping(UrlMapping newMapping);
}
