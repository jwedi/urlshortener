package com.johanwedin.urlshortener.controller;

import com.google.inject.Inject;
import com.johanwedin.urlshortener.db.UrlRepository;
import com.johanwedin.urlshortener.db.cassandra.entities.UrlMappingHelper__MapperGenerated;
import com.johanwedin.urlshortener.db.sentinels.CollisionException;
import com.johanwedin.urlshortener.helpers.hashing.HashStrategy;
import com.johanwedin.urlshortener.models.UrlMapping;
import com.johanwedin.urlshortener.models.sentinels.ApplicationException;
import com.johanwedin.urlshortener.models.sentinels.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UrlController {
    private static final Logger LOG = LoggerFactory.getLogger(UrlController.class);
    private static final int COLLISION_RETRY_THRESHOLD = 4;

    private final UrlRepository repository;
    private final HashStrategy hashStrategy;

    @Inject
    public UrlController(UrlRepository repository, HashStrategy hashStrategy) {
        this.repository = repository;
        this.hashStrategy = hashStrategy;
    }

    public UrlMapping getByUrlId(String urlId) {
        try {
            UrlValidator.validateUrlOrThrow(urlId, "urlId");
            Optional<UrlMapping> exists = repository.getByUrlId(urlId);
            return exists.orElseThrow(() -> new com.johanwedin.urlshortener.models.sentinels.NotFoundException(String.format("could not find mapping for urlId: %s", urlId)));
        } catch (Exception e) {
            if (e instanceof ApplicationException) {
                throw e;
            }
            throw new InternalException(e);
        }
    }

    private UrlMapping createUrlMapping(String originalUrl, int padding) {
        String urlId = this.hashStrategy.hash(originalUrl, padding);
        try {
            return this.repository.addMapping(new UrlMapping(urlId, originalUrl));
        } catch (CollisionException ex) {
            if (padding >= COLLISION_RETRY_THRESHOLD) {
                throw new InternalException(String.format("too many repeated hash collisions for: %s", originalUrl));
            }
            LOG.warn("urlId collision, retrying with mapping", ex);
            return this.createUrlMapping(originalUrl, padding+1);
        } catch (Exception e) {
            if (e instanceof ApplicationException) {
                throw e;
            }
            throw new InternalException(e);
        }
    }

    public UrlMapping createUrlMapping(String originalUrl) {
        UrlValidator.validateUrlOrThrow(originalUrl, "originalUrl");
        return this.createUrlMapping(originalUrl, 0);
    }
}
