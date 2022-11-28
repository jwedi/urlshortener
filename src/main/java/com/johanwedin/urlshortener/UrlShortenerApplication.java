package com.johanwedin.urlshortener;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.google.inject.*;
import com.google.inject.name.Names;
import com.johanwedin.urlshortener.db.UrlRepository;
import com.johanwedin.urlshortener.db.UrlRepositoryWrapper;
import com.johanwedin.urlshortener.db.cassandra.CassandraUrlRepositoryImpl;
import com.johanwedin.urlshortener.db.cassandra.SchemaManager;
import com.johanwedin.urlshortener.db.cassandra.dao.UrlDao;
import com.johanwedin.urlshortener.db.cassandra.mapper.UrlMapperBuilder;
import com.johanwedin.urlshortener.db.sentinels.CollisionException;
import com.johanwedin.urlshortener.db.sentinels.NotFoundException;
import com.johanwedin.urlshortener.helpers.hashing.HashStrategy;
import com.johanwedin.urlshortener.helpers.hashing.MurmurHashStrategy;
import com.johanwedin.urlshortener.helpers.resilience.ResilienceProvider;
import com.johanwedin.urlshortener.helpers.tracing.TracingConfiguration;
import dev.failsafe.*;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider.JerseyProviderInstaller;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

import javax.servlet.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class UrlShortenerApplication extends Application<UrlShortenerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new UrlShortenerApplication().run(args);
    }

    @Override
    public String getName() {
        return "UrlShortener";
    }

    @Override
    public void initialize(final Bootstrap<UrlShortenerConfiguration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.builder().installers(JerseyProviderInstaller.class)
                        .modules(new ConfigurationModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .build());

    }

    @Override
    public void run(final UrlShortenerConfiguration configuration,
                    final Environment environment) {
        Injector injector = InjectorLookup.getInstance(this, Injector.class).get();
        CollectorRegistry collectorRegistry = new CollectorRegistry();
        collectorRegistry.register(new DropwizardExports(environment.metrics()));
        environment.admin()
                .addServlet("prometheusMetrics", new MetricsServlet(collectorRegistry))
                .addMapping("/prometheusMetrics");
    }

    static class ConfigurationModule extends DropwizardAwareModule<UrlShortenerConfiguration> {

        @Override
        protected void configure() {

            FailsafeExecutor<Object> cassandraExecutor = buildDefaultFailsafeExecutor();
            ResilienceProvider cassandraResilienceProvider = new ResilienceProvider(cassandraExecutor);
            bind(ResilienceProvider.class).annotatedWith(Names.named("urlRepository")).toInstance(cassandraResilienceProvider);
            bind(UrlRepository.class).annotatedWith(Names.named("urlRepositoryDelegate")).to(CassandraUrlRepositoryImpl.class).in(Scopes.SINGLETON);
            bind(UrlRepository.class).to(UrlRepositoryWrapper.class).in(Scopes.SINGLETON);
            bind(HashStrategy.class).to(MurmurHashStrategy.class).in(Scopes.SINGLETON);

        }

        private FailsafeExecutor<Object> buildDefaultFailsafeExecutor() {
            RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                    .handle(ConnectException.class, AllNodesFailedException.class, IOException.class, CircuitBreakerOpenException.class)
                    .withDelay(Duration.ofSeconds(1))
                    .withMaxRetries(3)
                    .build();
            Timeout<Object> timeout = Timeout.of(Duration.ofSeconds(2));
            CircuitBreaker<Object> circuitBreaker = CircuitBreaker.builder()
                    .handle(ConnectException.class, IOException.class, TimeoutException.class, AllNodesFailedException.class)
                    .withFailureRateThreshold(20, 5, Duration.ofMinutes(1))
                    .withDelay(Duration.ofSeconds(30))
                    .withSuccessThreshold(5)
                    .build();
            return Failsafe.with(retryPolicy, circuitBreaker, timeout);
        }

        @Provides
        @Singleton
        public OpenTelemetry provideOpenTelementry() {
            return TracingConfiguration.initOpenTelemetry();
        }

        @Provides
        @Singleton
        public CqlSession provideCqlSession() {
            String host = configuration().getDbHost();
            int port = configuration().getDbPort();
            return CqlSession
                    .builder()
                    .addContactPoint(new InetSocketAddress(host, port))
                    .withLocalDatacenter("datacenter1")
                    .build();
        }

        @Provides
        @Singleton
        @Inject
        public UrlDao provideUrlDao(CqlSession cqlSession) {
            String applicationKeyspace = "app";
            int applicationKeyspaceReplication = 1;
            SchemaManager.initKeyspace(cqlSession, applicationKeyspace, applicationKeyspaceReplication);
            SchemaManager.initTables(cqlSession, applicationKeyspace);
            return new UrlMapperBuilder(cqlSession).build().urlDao(CqlIdentifier.fromCql(applicationKeyspace));
        }
    }

}
