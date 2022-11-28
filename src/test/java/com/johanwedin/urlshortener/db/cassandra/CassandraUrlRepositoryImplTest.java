package com.johanwedin.urlshortener.db.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.johanwedin.urlshortener.db.cassandra.dao.UrlDao;
import com.johanwedin.urlshortener.db.cassandra.mapper.UrlMapper;
import com.johanwedin.urlshortener.db.cassandra.mapper.UrlMapperBuilder;
import com.johanwedin.urlshortener.db.sentinels.CollisionException;
import com.johanwedin.urlshortener.models.UrlMapping;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.CassandraContainer;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CassandraUrlRepositoryImplTest {
    private static final String KEYSPACE_NAME = "test";
    private CqlSession session;

    private CassandraUrlRepositoryImpl repo;

    static CassandraContainer<?> cassandra;
    @BeforeAll
    static void beforeAll() {
        cassandra = new CassandraContainer<>("cassandra:4.1");
        cassandra.start();
    }

    @AfterAll
    static void afterAll() {
        cassandra.stop();
    }

    @BeforeEach
    public void setUp() {
        this.session = CqlSession
                .builder()
                .addContactPoint(cassandra.getContactPoint())
                .withLocalDatacenter(cassandra.getLocalDatacenter())
                .build();
        SchemaManager.initKeyspace(session, KEYSPACE_NAME, 1);
        SchemaManager.initTables(session, KEYSPACE_NAME);
        UrlMapper urlMapper = new UrlMapperBuilder(session).withDefaultKeyspace(KEYSPACE_NAME).build();
        UrlDao ud = urlMapper.urlDao(CqlIdentifier.fromCql(KEYSPACE_NAME));
        repo = new CassandraUrlRepositoryImpl(ud);
    }

    @AfterEach
    public void tearDown() {
    }


    @Test
    void create_shouldReturnExistingOnIdenticalCollision() {
        // Given
        String randomShortName = UUID.randomUUID().toString();
        UrlMapping mapping = new UrlMapping(randomShortName, "long");
        // When
        UrlMapping stored = repo.addMapping(mapping);
        UrlMapping storedAgain = repo.addMapping(mapping);
        // Then
        assertThat(stored).isEqualTo(storedAgain);
    }

    @Test
    void get_shouldReturnEmptyOptionalOnUnknownId() {
        // Given
        String randomShortName = UUID.randomUUID().toString();
        // When
        Optional<UrlMapping> stored = repo.getByUrlId(randomShortName);
        // Then
        assertThat(stored).isEmpty();
    }

    @Test
    void create_shouldThrowOnCollisionForDifferentUrl() {
        // Given
        String randomShortName = UUID.randomUUID().toString();
        UrlMapping mapping = new UrlMapping(randomShortName, "long");
        UrlMapping mappingWithDifferentLongUrl = new UrlMapping(randomShortName, "different");
        // When
        UrlMapping stored = repo.addMapping(mapping);
        assertThrows(CollisionException.class, () -> repo.addMapping(mappingWithDifferentLongUrl));
    }

    @Test
    void create_shouldStoreAndReadBackSameEntity() {
        // Given
        String randomShortName = UUID.randomUUID().toString();
        UrlMapping newMapping = new UrlMapping(randomShortName, "long");
        UrlMapping stored = repo.addMapping(newMapping);
        // When
        Optional<UrlMapping> readBack = repo.getByUrlId(stored.getUrlId());
        // Then
        assertThat(stored).isEqualTo(newMapping);
        assertThat(readBack).isNotEmpty();
        assertThat(readBack.get()).isEqualTo(stored);

    }
}