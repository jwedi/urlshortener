package com.johanwedin.urlshortener.db.cassandra;

import com.google.inject.Inject;
import com.johanwedin.urlshortener.db.UrlRepository;
import com.johanwedin.urlshortener.db.cassandra.dao.UrlDao;
import com.johanwedin.urlshortener.db.sentinels.CollisionException;
import com.johanwedin.urlshortener.models.UrlMapping;

import java.util.Optional;

public class CassandraUrlRepositoryImpl implements UrlRepository {
    @Inject
    private UrlDao repo;

    @Inject
    public CassandraUrlRepositoryImpl(UrlDao repo) {
        this.repo = repo;
    }

    @Override
    public Optional<UrlMapping> getByUrlId(String urlId) {
        return repo.findById(urlId).map(urlMapping -> new UrlMapping(urlMapping.getUrlId(), urlMapping.getOriginalUrl()));
    }

    @Override
    public UrlMapping addMapping(UrlMapping newMapping) {
        // Insert if not exists, if exists, check if matches, if yes, return UrlMapping, else throw CollisionException
        Optional<com.johanwedin.urlshortener.db.cassandra.entities.UrlMapping> exists = repo.insertIfNotExists(new com.johanwedin.urlshortener.db.cassandra.entities.UrlMapping(newMapping.getUrlId(), newMapping.getOriginalUrl()));
        if (exists.isPresent()) {
            if (exists.get().getOriginalUrl().equals(newMapping.getOriginalUrl())) {
                // Duplicate, return existing entity
                return newMapping;
            } else {
                // Collision for different long url
                throw new CollisionException(String.format("collision on urlId %s for both existing: %s and new %s", newMapping.getUrlId(), exists.get().getOriginalUrl(), newMapping.getOriginalUrl()));
            }
        }
        // Stored new value without collision
        return newMapping;
    }
}
