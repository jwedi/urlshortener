package com.johanwedin.urlshortener.db;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.johanwedin.urlshortener.helpers.resilience.ResilienceProvider;
import com.johanwedin.urlshortener.models.UrlMapping;
import com.johanwedin.urlshortener.models.sentinels.UnavailableException;
import dev.failsafe.FailsafeException;
import dev.failsafe.FailsafeExecutor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import java.util.Optional;

public class UrlRepositoryWrapper implements UrlRepository {
    private static final int MAX_CACHE_SIZE = 10000;

    private final UrlRepository delegate;
    private final FailsafeExecutor<Object> failsafeExecutor;

    private final LoadingCache<String, Optional<UrlMapping>> urlMappingCache;
    private final Tracer tracer;

    @Inject
    public UrlRepositoryWrapper(@Named(value = "urlRepositoryDelegate") UrlRepository delegate, @Named(value = "urlRepository") ResilienceProvider rs, OpenTelemetry openTelemetry) {
        this.delegate = delegate;
        this.failsafeExecutor = rs.get();
        this.tracer = openTelemetry.getTracer(UrlRepositoryWrapper.class.getName());
        this.urlMappingCache = CacheBuilder.newBuilder().maximumSize(MAX_CACHE_SIZE).build(
                new CacheLoader<String, Optional<UrlMapping>>() {
                    @Override
                    public Optional<UrlMapping> load(String key) {
                        return delegate.getByUrlId(key);
                    }
                });
    }

    @Override
    public Optional<UrlMapping> getByUrlId(String urlId) {
        Span span = tracer.spanBuilder("getByUrlId").startSpan();
        try {
            return failsafeExecutor.get(() -> urlMappingCache.getUnchecked(urlId));
        } catch (FailsafeException ex) {
            span.recordException(ex);
            throw new UnavailableException(ex);
        } finally {
            span.end();
        }
    }

    @Override
    public UrlMapping addMapping(UrlMapping newMapping) {
        Span span = tracer.spanBuilder("addMapping").startSpan();
        try {
            return failsafeExecutor.get(() -> delegate.addMapping(newMapping));
        } catch (FailsafeException ex) {
            span.recordException(ex);
            throw new UnavailableException(ex);
        } finally {
            span.end();
        }
    }
}
